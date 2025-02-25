package com.openlab.reservation_service.model;

public record PaymentRequest(String reservationId, double amount, String cardNumber) {

}
