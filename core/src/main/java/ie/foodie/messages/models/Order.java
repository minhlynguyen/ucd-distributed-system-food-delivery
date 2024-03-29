package ie.foodie.messages.models;

import java.util.List;

import ie.foodie.messages.MessageSerializable;

public class Order implements MessageSerializable {
    private Restaurant restaurant;
    private List<OrderDetail> orderDetails;

    public Order(Restaurant restaurant, List<OrderDetail> orderDetails) {
        this.restaurant = restaurant;
        this.orderDetails = orderDetails;
    }

    public Order() {
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public static class Restaurant {
        private int restaurantId;
        private String restaurantPhone;
        private String restaurantAddress;

        public Restaurant(int restaurantId, String restaurantPhone, String restaurantAddress) {
            this.restaurantId = restaurantId;
            this.restaurantPhone = restaurantPhone;
            this.restaurantAddress = restaurantAddress;
        }

        public int getRestaurantId() {
            return restaurantId;
        }

        public void setRestaurantId(int restaurantId) {
            this.restaurantId = restaurantId;
        }

        public String getRestaurantPhone() {
            return restaurantPhone;
        }

        public void setRestaurantPhone(String restaurantPhone) {
            this.restaurantPhone = restaurantPhone;
        }

        public String getRestaurantAddress() {
            return restaurantAddress;
        }

        public void setRestaurantAddress(String restaurantAddress) {
            this.restaurantAddress = restaurantAddress;
        }
    }

    public static class OrderDetail {
        private int foodId;
        private double price;
        private int quantity;

        public OrderDetail(int foodId, double price, int quantity) {
            this.foodId = foodId;
            this.price = price;
            this.quantity = quantity;
        }

        public int getFoodId() {
            return foodId;
        }

        public void setFoodId(int foodId) {
            this.foodId = foodId;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}