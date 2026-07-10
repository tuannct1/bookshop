package com.example.bookshop.entity;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "receiver_name", length = 50)
    private String receiverName;
    @Column(name = "receiver_address", length = 100)
    private String receiverAddress;
    @Column(name = "receiver_phone", length = 15)
    private String receiverPhone;
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    @Column(name = "payment_status", length = 50)
    private String paymentStatus;
    @Column(name = "status", length = 50)
    private String status;
    @Column(name = "total_price")
    private double totalPrice;
    @Column(name = "note", length = 200)
    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "order")
    private Set<OrderDetail> orderDetails;
}
