package ie.foodie.printer;

import java.util.List;

import ie.foodie.messages.*;
import ie.foodie.messages.models.Customer;
import ie.foodie.messages.models.Order;
import ie.foodie.messages.models.Order.OrderDetail;

public class MessagePrinter {

    private static final String SEPARATOR = "------------------------------------------";

    public static void printRestaurantOrderMessage(RestaurantOrderMessage message) {
        if (message == null) {
            System.out.println("RestaurantOrderMessage is null.");
            return;
        }

        System.out.println("Restaurant Order Message Details:");
        System.out.println("Customer ID: " + message.getCustomerId());

        Order order = message.getOrder();
        if (order != null) {
            printOrder(order);
        } else {
            System.out.println("Order details are not available.");
        }
    }

    private static void printOrder(Order order) {
        Order.Restaurant restaurant = order.getRestaurant();
        if (restaurant != null) {
            System.out.println("Restaurant ID: " + restaurant.getRestaurantId());
            System.out.println("Restaurant Phone: " + restaurant.getRestaurantPhone());
            System.out.println("Restaurant Address: " + restaurant.getRestaurantAddress());
        } else {
            System.out.println("Restaurant details are not available.");
        }

        List<OrderDetail> orderDetails = order.getOrderDetails();
        if (orderDetails != null) {
            System.out.println("\nOrder Details:");
            for (Order.OrderDetail detail : orderDetails) {
                System.out.println(" - Food ID: " + detail.getFoodId() +
                        ", Price: " + detail.getPrice() +
                        ", Quantity: " + detail.getQuantity());
            }
        } else {
            System.out.println("Order items are not available.");
        }
        System.out.println();
    }

    // public static void printPaymentConfirmMessage(PaymentConfirmMessage message) {
    public static void printPaymentConfirmMessage(PaymentStatusMessage message) {
        if (message == null) {
            System.out.println("Payment Confirm Message is null.");
        } else {
            System.out.println("PaymentConfirmMessage Details:");
            System.out.println("Order ID: " + message.getOrderId());
            System.out.println("Status: " + message.getStatus());
            System.out.println();
        }
    }

    public static void printCustomerOrderMessage(CustomerOrderMessage customerOrderMessage) {
        if (customerOrderMessage == null) {
            System.out.println("No Customer Order Message available.");
            return;
        }

        Customer customer = customerOrderMessage.getCustomer();
        System.out.println("Customer Details:");
        System.out.println("Customer ID: " + customer.getCustomerId());
        System.out.println("Customer Address: " + customer.getCustomerAddress());
        System.out.println("Customer Phone: " + customer.getCustomerPhone());

        List<Order> orders = customerOrderMessage.getOrders();
        if (orders == null || orders.size() == 0) {
            System.out.println("No customer order details available.");
            return;
        }

        System.out.println("\nOrder Details:");
        for (Order order : orders) {
            Order.Restaurant restaurant = order.getRestaurant();
            System.out.println("Restaurant ID: " + restaurant.getRestaurantId());
            System.out.println("Restaurant Phone: " + restaurant.getRestaurantPhone());
            System.out.println("Restaurant Address: " + restaurant.getRestaurantAddress());

            List<OrderDetail> orderDetails = order.getOrderDetails();
            if (orderDetails == null || orderDetails.size() == 0) {
                System.out.println("No order details available for this restaurant.");
                continue;
            }

            System.out.println("\nFood Ordered:");
            for (Order.OrderDetail detail : orderDetails) {
                System.out.println(" - Food ID: " + detail.getFoodId() + ", Price: " + detail.getPrice()
                        + ", Quantity: " + detail.getQuantity());
            }
            System.out.println("******************************************");
        }
    }

    public static void printOrderDeliveryMessage(OrderDeliveryMessage message) {
        if (message == null) {
            System.out.println("OrderDeliveryMessage is null.");
            return;
        }

        System.out.println("Order Delivery Message Details:");

        // Print Customer ID
        Customer customer = message.getCustomer();
        if (customer != null) {
            System.out.println("Customer ID: " + customer.getCustomerId());
        } else {
            System.out.println("Customer details are not available.");
        }

        // Print Restaurant ID
        Order order = message.getOrder();
        if (order != null) {
            Order.Restaurant restaurant = order.getRestaurant();
            if (restaurant != null) {
                System.out.println("Restaurant ID: " + restaurant.getRestaurantId());
            } else {
                System.out.println("Restaurant details are not available.");
            }
        } else {
            System.out.println("Order details are not available.");
        }

        System.out.println(SEPARATOR);
    }
}
