package ie.foodie.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestKit;
import ie.foodie.messages.CustomerOrderMessage;
import ie.foodie.messages.OrderConfirmMessage;
import ie.foodie.messages.models.Customer;
import ie.foodie.messages.models.Order;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

public class OrderServiceTest {
    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system, Duration.apply(2, TimeUnit.SECONDS), false);
        system = null;
    }

    @Test
    public void testSendCustomerOrderMessage() {
        CustomerOrderMessage customerOrderMessage = generateCustomerOrderMessage();
        final Props props = Props.create(OrderService.class);
        final ActorRef orderService = system.actorOf(props); // orderService
        final TestKit customer = new TestKit(system); // customer

        // server send a RegisterMessage to broker
        orderService.tell(customerOrderMessage, customer.testActor());
        OrderConfirmMessage orderConfirmMessage = customer.expectMsgClass(FiniteDuration.apply(10, TimeUnit.SECONDS), OrderConfirmMessage.class);

        Assert.assertEquals(1, orderConfirmMessage.getOrderId());
        Assert.assertEquals(91.98, orderConfirmMessage.getTotalPrice(), 0);
    }

    private CustomerOrderMessage generateCustomerOrderMessage() {
        // Create a customer
        Customer customer = new Customer(1, "123 Main Street", "555-1234");

        // Create a restaurant
        Order.Restaurant restaurant1 = new Order.Restaurant(101, "555-1001", "456 Park Ave");
        Order.Restaurant restaurant2 = new Order.Restaurant(102, "555-1002", "789 Broadway");

        // Create order details for restaurant 1
        Order.OrderDetail[] detailsForRestaurant1 = {
                new Order.OrderDetail(501, 9.99, 2), // 2 units of food item 501
                new Order.OrderDetail(502, 12.50, 1) // 1 unit of food item 502
        };

        // Create order details for restaurant 2
        Order.OrderDetail[] detailsForRestaurant2 = {
                new Order.OrderDetail(503, 15.00, 3), // 3 units of food item 503
                new Order.OrderDetail(504, 7.25, 2)  // 2 units of food item 504
        };

        // Create orders for each restaurant
        Order order1 = new Order(restaurant1, detailsForRestaurant1);
        Order order2 = new Order(restaurant2, detailsForRestaurant2);

        // Array of orders
        Order[] orders = {order1, order2};

        // Create the CustomerOrderMessage
        return new CustomerOrderMessage(customer, orders);
    }
}