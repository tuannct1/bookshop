package com.example.bookshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "txn_ref", nullable = false, unique = true, length = 100)
    private String txnRef; 

    @Column(name = "transaction_no", length = 100)
    private String transactionNo; 

    @Column(name = "amount", nullable = false)
    private double amount; 

    @Column(name = "bank_code", length = 20)
    private String bankCode;

    @Column(name = "response_code", length = 20)
    private String responseCode; 
    @Column(name = "status", nullable = false, length = 50)
    private String status; 

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}