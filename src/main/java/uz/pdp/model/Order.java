package uz.pdp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private UUID id = UUID.randomUUID();
    private User user;
    private Book book;
    private Integer amount;
    private Payment payment;
    private OrderStatus orderStatus;
    private Timestamp orderDate;
    private Float longitude;
    private Float latitude;
    private String address;

    public Order(User user, Book book, Integer amount, OrderStatus orderStatus) {
        this.user = user;
        this.book = book;
        this.amount = amount;
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "Order{" +
                "book=" + book +
                ", amount=" + amount +
                ", payment=" + payment +
                ", orderStatus=" + orderStatus +
                ", orderDate=" + orderDate +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", address='" + address + '\'' +
                '}';
    }
}
