package ie.foodie;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import ie.foodie.actors.ActorAllocator;
import ie.foodie.services.DeliveryService;
import ie.foodie.services.DriverService;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("delivery-system");

        final Props DeliveryServiceProp = Props.create(DeliveryService.class);
        final ActorRef deliveryActorRef
                = system.actorOf(DeliveryServiceProp, "delivery-service");

        final Props DriverServiceProp = Props.create(DriverService.class);
        final ActorRef driverActorRef
                =system.actorOf(DriverServiceProp, "driver-service");

        System.out.println("Delivery Service (Driver Allocator) Starts.");
        try {
            ActorSelection orderActor = system.actorSelection("akka.tcp://order-system@order-host:2553/user/order-service");
            System.out.println("Connect to remote order Actor: " + orderActor + " successfully.");
        } catch (Exception e) {
            System.out.println("Connect to remote order Actor Failed.");
            e.printStackTrace();
        }
    }
}