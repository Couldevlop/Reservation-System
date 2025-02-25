package com.openlab.reservation_service.service;

import com.openlab.reservation_service.model.dto.ReservationDTO;
import com.openlab.reservation_service.model.dto.ReservationResponseDTO;

import java.util.List;

public interface ReservationService {

    ReservationResponseDTO createReservation(ReservationDTO dto);
    ReservationResponseDTO getReservationById(String id);
     List<ReservationResponseDTO> getAllReservations();
    ReservationResponseDTO updateReservation(String id, ReservationDTO DTO);

     void deleteReservation(String id);

}
