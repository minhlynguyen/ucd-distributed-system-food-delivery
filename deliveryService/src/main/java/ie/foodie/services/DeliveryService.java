package ie.foodie.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ie.foodie.actors.FoodieActor;
import scala.concurrent.duration.Duration;
import akka.actor.*;
import ie.foodie.actors.DriverActorProvider;
import ie.foodie.messages.*;
import ie.foodie.services.SSE.SSEController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class DeliveryService extends FoodieActor {
    private SSEController sseController;
    private ActorSelection driverServiceActor;
    private ActorRef orderServiceActor = null;

    public DeliveryService(SSEController sseController) {
        this.sseController = sseController;
    }

    @Override
    public void preStart() {
        ActorSystem system = getContext().getSystem();
        this.driverServiceActor = DriverActorProvider.getDriverActor(system);
    }

    @Override
    public Receive createReceive(){
        return receiveBuilder()
                .match(OrderDeliveryMessage.class, this::processDelivery)
                .match(DeliveryQueryMessage.class, this::msgForwarder)
                .build();
    }

    private void msgForwarder(DeliveryQueryMessage message) throws JsonProcessingException {
        // SSE
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString("Delivery query has been received for order ID: " + message.getOrderId());
        sseController.sendMessageToClients(jsonMessage);

        switch (message.getStatus()) {
            case "Dispatched" :
                orderServiceActor.tell(message, getSelf());
                break;

            case "NoDriver" :
                System.out.println("No available driver matched the order: "+ message.getOrderId() +" at this time." + "\n"
                        + "But we will try our best to allocate one.\n");
                getContext().system().scheduler().scheduleOnce(
                        Duration.create(60, TimeUnit.SECONDS),
                        getSender(),
                        new DriverService.InternalMsgSlaveDriver(message.getOrderId(), message.getUserRef()),
                        getContext().dispatcher(),
                        getSelf()
                );
                orderServiceActor.tell(message, getSelf());
                break;

            case "Delivered" :
                DriverService.InternalMsgFreeDriver msgFreeDriver = new DriverService.InternalMsgFreeDriver(
                        message.getOrderId(), Integer.parseInt(message.getMessage()), message.getUserRef());
                driverServiceActor.tell(msgFreeDriver, getSelf());

                DeliveryQueryMessage deliveryQueryMessage = new DeliveryQueryMessage(
                        message.getOrderId(), "Delivered",
                        "Your order " + message.getOrderId()
                                + " is delivered at "
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "."
                , message.getUserRef());
                orderServiceActor.tell(deliveryQueryMessage, getSelf());
                System.out.println("Order: " + message.getOrderId() + " is delivered.\n");
                break;
        }

    }

    private void processDelivery(OrderDeliveryMessage message) throws JsonProcessingException {
        this.orderServiceActor = getSender();
        int orderId = message.getOrderId();
        String customerAddress = message.getCustomer().getCustomerAddress();
        String customerPhone = message.getCustomer().getCustomerPhone();
        String restaurantAddress = message.getOrder().getRestaurant().getRestaurantAddress();
        String restaurantPhone = message.getOrder().getRestaurant().getRestaurantPhone();

        // SSE
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString("Delivery requested for order ID: " + orderId );
        sseController.sendMessageToClients(jsonMessage);

        System.out.println("NEW DELIVER TASK CREATED" + "\n"
                    + "Order ID: " + orderId + "\n"
                    + "Customer Address: " + customerAddress + "\n"
                    + "Customer Phone: " + customerPhone + "\n"
                    + "Restaurant Address: " + restaurantAddress + "\n"
                    + "Restaurant Phone: " + restaurantPhone + "\n");
        System.out.println("We are allocating suitable driver at this time.\n");
        DriverService.InternalMsgSlaveDriver internalMsgSlaveDriver = new DriverService.InternalMsgSlaveDriver(
                message.getOrderId(), message.getUserRef());
        driverServiceActor.tell(internalMsgSlaveDriver, getSelf());

        DeliveryQueryMessage deliveryQueryMessage = new DeliveryQueryMessage(orderId,
                "Pending", "Allocating suitable driver.", message.getUserRef());
        orderServiceActor.tell(deliveryQueryMessage, getSelf());
    }
}
