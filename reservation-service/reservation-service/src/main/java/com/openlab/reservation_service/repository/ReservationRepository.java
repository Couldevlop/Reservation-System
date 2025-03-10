package com.openlab.reservation_service.repository;

import com.openlab.reservation_service.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
    Reservation findByCarId(String id);
}
