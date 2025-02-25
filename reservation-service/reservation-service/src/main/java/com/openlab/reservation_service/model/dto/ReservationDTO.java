package com.openlab.reservation_service.model.dto;

import com.openlab.reservation_service.model.Reservation;

public record ReservationDTO(String id,
                             String carId,
                             String userId,
                             String status,
                             String cardNumber,
                             double amount) {
    public static ReservationDTO fromEntity(Reservation reservation){
        return new ReservationDTO(reservation.getId(),
                reservation.getCarId(),
                reservation.getUserId(),
                reservation.getStatus(),
                null,
                0.0);

    }

    public Reservation toEntity(){
        return new Reservation(id, carId, userId, status);
    }
}
