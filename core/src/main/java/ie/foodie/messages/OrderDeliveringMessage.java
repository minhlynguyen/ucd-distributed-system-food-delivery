package ie.foodie.messages;

public class OrderDeliveringMessage implements MessageSerializable {
    private int orderId;
    private String status;
    private String message;

    public OrderDeliveringMessage(int orderId, String status, String message) {
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    public OrderDeliveringMessage() {
    }

    public int getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "OrderDeliveringMessage{" +
                "orderId=" + orderId +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
