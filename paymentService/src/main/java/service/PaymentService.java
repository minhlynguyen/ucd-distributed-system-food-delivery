package service;

import java.util.Scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ie.foodie.actors.FoodieActor;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import actors.ActorProvider;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import ie.foodie.messages.*;
import service.SSE.SSEController;

public class PaymentService extends FoodieActor {
    
    private ActorSelection orderServiceActor;
    //private ActorSelection userActor;
    private SSEController sseController;

    public PaymentService(SSEController sseController) {
        this.sseController = sseController;
    }

    @Override
    public void preStart() {
        ActorSystem system = getContext().getSystem();
        this.orderServiceActor = ActorProvider.getOrderServiceActor(system);
//        this.userActor = ActorProvider.getUserActor(system);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderPaymentMessage.class, this::processPayment)
                .build();
    }

    private void processPayment(OrderPaymentMessage message) throws JsonProcessingException {

        int orderId = message.getOrderId();
        double totalPrice = message.getTotalPrice();
        String paymentMethod = message.getPaymentMethod();
//        ActorRef sender = getSender(); // Storing the reference to the sender (UserActor)

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString("Processing payment for Order ID: " + orderId +
                ", Total Price: " + totalPrice +
                ", Payment Method: " + paymentMethod);
        sseController.sendMessageToClients(jsonMessage);

        System.out.println("Processing payment for Order ID: " + orderId +
                           ", Total Price: " + totalPrice + 
                           ", Payment Method: " + paymentMethod);

        if (paymentMethod.equalsIgnoreCase("Card")){
            boolean isPaymentSuccessful = processWithPaymentGateway(message);            
        
            if (isPaymentSuccessful) {
                // Send success message to OrderService
                PaymentStatusMessage statusMessage = new PaymentStatusMessage(message.getOrderId(), "CARD-PAID", "Payment processed successfully.", message.getUserRef());
                
                if (getSender() != null) {
                    getSender().tell(statusMessage, getSelf());
                    System.out.println("Confirmation sent to Order Service.");
                } else {                
                    System.out.println("Could not find Order Service.");
                }
        
                // Send success message to UserActor
                //sender.tell(statusMessage, getSelf());
                // System.out.println(statusMessage);
            } else {

                // Handle payment failure scenario
                PaymentStatusMessage statusMessage = new PaymentStatusMessage(message.getOrderId(), "CARD-UNPAID", "Payment processing failed.", message.getUserRef());
                getSender().tell(statusMessage, getSelf());
            }
        } else {
            PaymentStatusMessage statusMessage = new PaymentStatusMessage(message.getOrderId(), "CASH-UNPAID", "Driver will collect cash.", message.getUserRef());
            if (getSender() != null) {
                getSender().tell(statusMessage, getSelf());
                System.out.println("Confirmation sent to Order Service.");
            } else {                
                System.out.println("Could not find Order Service.");
            }
//            sender.tell(statusMessage, getSelf());
        }
    }

    private boolean processWithPaymentGateway(OrderPaymentMessage message) {
        // Implement actual payment processing with Stripe or another gateway
        // This should include API calls to Stripe and handling responses
        // For now, let's assume the payment is always successful
        return true; // Placeholder for actual implementation
    }
}
