package com.openlab.payment_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment implements Serializable {
    @Id
    private String id;
    private String reservationId;
    private double amount;
    private String cardNumber;
    private LocalDateTime createdAt;
    private boolean successful;

    public Payment() {
    }

    public Payment(String id, String reservationId, double amount, String cardNumber, LocalDateTime createdAt, boolean successful) {
        this.id = id;
        this.reservationId = reservationId;
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.createdAt = createdAt;
        this.successful = successful;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
