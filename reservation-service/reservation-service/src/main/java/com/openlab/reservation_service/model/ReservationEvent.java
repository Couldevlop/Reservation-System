package com.openlab.reservation_service.model;



public record ReservationEvent(String reservationId, String status) {}