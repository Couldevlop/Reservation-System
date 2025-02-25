package com.openlab.reservation_service.service.impl;

import com.openlab.reservation_service.client.CatalogServiceClient;
import com.openlab.reservation_service.client.PaymentServiceClient;
import com.openlab.reservation_service.exception.ReservationAlreadyExistException;
import com.openlab.reservation_service.exception.ReservationNotFoundException;
import com.openlab.reservation_service.model.*;
import com.openlab.reservation_service.model.dto.CarDTO;
import com.openlab.reservation_service.model.dto.ReservationDTO;
import com.openlab.reservation_service.model.dto.ReservationResponseDTO;
import com.openlab.reservation_service.repository.ReservationRepository;
import com.openlab.reservation_service.service.ReservationService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final KafkaTemplate<String, ReservationEvent> kafkaTemplate;
    private final CircuitBreaker circuitBreaker;
    private final CatalogServiceClient catalogServiceClient;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  PaymentServiceClient paymentServiceClient,
                                  KafkaTemplate<String, ReservationEvent> kafkaTemplate,
                                  CircuitBreakerRegistry circuitBreakerRegistry,
                                  CatalogServiceClient catalogServiceClient) {
        this.reservationRepository = reservationRepository;
        this.paymentServiceClient = paymentServiceClient;
        this.kafkaTemplate = kafkaTemplate;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("payment");
        this.catalogServiceClient = catalogServiceClient;
    }

    @Override
    public ReservationResponseDTO createReservation(ReservationDTO dto) {
        Reservation reservation = dto.toEntity();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setStatus("PENDING");

        CarDTO car = catalogServiceClient.getByCardId(dto.carId());
       if(!car.available()){
           reservation.setStatus("CANCELLED");
           reservationRepository.save(reservation);
           kafkaTemplate.send("Reservation", new ReservationEvent(reservation.getId(), "CANCELLED"));
           throw new IllegalArgumentException("Car with id: " + dto.carId() + "is not available");
       }

        // Eviter les doublons
         if(!reservationRepository.findAll().isEmpty()){
             if(Objects.equals(reservationRepository.findByCarId(dto.carId()).getCarId(), dto.carId())){
            reservation.setStatus("CANCELLED");
            reservationRepository.save(reservation);
            kafkaTemplate.send("Reservation", new ReservationEvent(reservation.getId(), "CANCELLED"));
            throw new ReservationAlreadyExistException("Car with id: " + dto.carId() + "is already reserved");
        }

         }

        // Appel au Payment Service avec Circuit Breaker
        PaymentRequest paymentRequest = new PaymentRequest(reservation.getId(), dto.amount(), dto.cardNumber());
        Supplier<PaymentResponse> paymentCall = () -> paymentServiceClient.createPayment(paymentRequest);
        PaymentResponse paymentResponse;
        try {
            paymentResponse = circuitBreaker.executeSupplier(paymentCall);
        } catch (Exception e) {
            // Fallback en cas d'échec
            reservation.setStatus("CANCELLED");
            reservationRepository.save(reservation);
            kafkaTemplate.send("reservations", new ReservationEvent(reservation.getId(), "CANCELLED"));
            throw new RuntimeException("Payment service unavailable: " + e.getMessage());
        }

        // Gestion du résultat du paiement
        if (!paymentResponse.successful()) {
            reservation.setStatus("CANCELLED");
            reservationRepository.save(reservation);
            kafkaTemplate.send("reservations", new ReservationEvent(reservation.getId(), "CANCELLED"));
            throw new RuntimeException("Payment failed");
        }

        // Paiement réussi
        reservation.setStatus("CONFIRMED");
        Reservation reservationSaved = reservationRepository.save(reservation);
        kafkaTemplate.send("reservations", new ReservationEvent(reservation.getId(), "CONFIRMED"));
        return ReservationResponseDTO.fromEntity(reservationSaved);
    }

    @Override
    public ReservationResponseDTO getReservationById(String id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " +id));

        return ReservationResponseDTO.fromEntity(reservation);
    }

    @Override
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream().map(
                ReservationResponseDTO::fromEntity).toList();
    }

    @Override
    public ReservationResponseDTO updateReservation(String id, ReservationDTO dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id:" +id));
        reservation.setCarId(dto.carId());
        reservation.setStatus(dto.status());
        reservation.setUserId(dto.userId());
        Reservation reservationUpdated = reservationRepository.save(reservation);
        kafkaTemplate.send("reservations", new ReservationEvent(reservation.getId(), reservation.getStatus()));
        return ReservationResponseDTO.fromEntity(reservationUpdated);
    }

    @Override
    public void deleteReservation(String id) {
        if( !reservationRepository.existsById((id))){
            throw  new ReservationNotFoundException("Reservation not found with id: " + id);
        }
        reservationRepository.deleteById(id);
        kafkaTemplate.send("reservations", new ReservationEvent(id, "DELETED") );

    }
}
