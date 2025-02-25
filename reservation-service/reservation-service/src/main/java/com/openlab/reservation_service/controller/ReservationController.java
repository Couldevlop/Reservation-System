package com.openlab.reservation_service.controller;

import com.openlab.reservation_service.model.dto.ReservationDTO;
import com.openlab.reservation_service.model.dto.ReservationResponseDTO;
import com.openlab.reservation_service.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDTO createReservation(@RequestBody ReservationDTO dto){
        return reservationService.createReservation(dto);
    }


    @GetMapping
    public List<ReservationResponseDTO> getAllReservations(){
        return reservationService.getAllReservations();
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReservationResponseDTO getReservationById(@PathVariable String id){
        return reservationService.getReservationById(id);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReservationResponseDTO updateReservation(@PathVariable String id, @RequestBody ReservationDTO dto){
        return reservationService.updateReservation(id, dto);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable String id){
        reservationService.deleteReservation(id);
        return ResponseEntity.ok("Rservation with id: " +id+ "is succesfully deleted");
    }
}
