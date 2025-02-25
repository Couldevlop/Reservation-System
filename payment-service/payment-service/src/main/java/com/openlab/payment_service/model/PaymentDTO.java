package com.openlab.payment_service.model;

import java.time.LocalDateTime;

public record PaymentDTO(String id, String reservationId, double amount, String cardNumber, LocalDateTime createdAt, boolean successful) {

    public static PaymentDTO fromEntity(Payment payment){
        return new PaymentDTO(
                payment.getId(),
                payment.getReservationId(),
                payment.getAmount(),
                payment.getCardNumber(),
                payment.getCreatedAt(),
                payment.isSuccessful()
        );
    }


    public Payment toEntity(){
        return new Payment(id, reservationId,amount, cardNumber, createdAt, successful);
    }
}
