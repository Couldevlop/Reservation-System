package com.openlab.reservation_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
@Entity
@Table(name = "reservations")
public class Reservation implements Serializable {
    @Id
    private String id;
    private String carId;
    private String userId;
    private String status; //"PENDING", "CONFIRMED", "CANCELLED"


    public Reservation() {
    }

    public Reservation(String id, String carId, String userId, String status) {
        this.id = id;
        this.carId = carId;
        this.userId = userId;
        this.status = status;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
