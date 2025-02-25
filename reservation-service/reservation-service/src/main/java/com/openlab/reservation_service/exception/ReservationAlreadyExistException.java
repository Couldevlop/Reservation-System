package com.openlab.reservation_service.exception;

public class ReservationAlreadyExistException extends RuntimeException{
    public ReservationAlreadyExistException(String message) {
        super(message);
    }
}
