package com.openlab.reservation_service.model.dto;

import com.openlab.reservation_service.model.Reservation;

public record ReservationResponseDTO(
        String id,
        String carId,
        String userId,
        String status
) {
    public static ReservationResponseDTO fromEntity(Reservation reservation) {
        return new ReservationResponseDTO(
                reservation.getId(),
                reservation.getCarId(),
                reservation.getUserId(),
                reservation.getStatus()
        );
    }
}