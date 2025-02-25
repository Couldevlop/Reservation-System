package com.openlab.reservation_service;

import com.openlab.reservation_service.client.CatalogServiceClient;
import com.openlab.reservation_service.client.PaymentServiceClient;
import com.openlab.reservation_service.exception.ReservationNotFoundException;
import com.openlab.reservation_service.model.*;
import com.openlab.reservation_service.model.dto.CarDTO;
import com.openlab.reservation_service.model.dto.ReservationDTO;
import com.openlab.reservation_service.model.dto.ReservationResponseDTO;
import com.openlab.reservation_service.repository.ReservationRepository;
import com.openlab.reservation_service.service.impl.ReservationServiceImpl;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTests {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @Mock
    private CatalogServiceClient catalogServiceClient;

    @Mock
    private KafkaTemplate<String, ReservationEvent> kafkaTemplate;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Mock
    private CircuitBreaker circuitBreaker;

    private ReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
        // Initialisation explicite des mocks
        MockitoAnnotations.openMocks(this);

        // Configuration des mocks
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.existsById(anyString())).thenReturn(true);

        when(paymentServiceClient.createPayment(any(PaymentRequest.class)))
                .thenReturn(new PaymentResponse("payment1", true));

        when(catalogServiceClient.getByCardId(any(String.class)))
                .thenReturn(new CarDTO("1","car1",true));

        when(kafkaTemplate.send(any(), any())).thenReturn(null);

        when(circuitBreakerRegistry.circuitBreaker("payment")).thenReturn(circuitBreaker);
        when(circuitBreaker.executeSupplier(any())).thenAnswer(invocation -> {
            Supplier<PaymentResponse> supplier = invocation.getArgument(0);
            return supplier.get();
        });

        // Initialisation manuelle pour garantir que circuitBreaker est injecté
        reservationService = new ReservationServiceImpl(reservationRepository,
                paymentServiceClient,
                kafkaTemplate,
                circuitBreakerRegistry,
                catalogServiceClient);
    }

    @Test
    void shouldCreateReservation() {
        ReservationDTO dto = new ReservationDTO(null, "car1", "user1", "PENDING", "4123456789012345", 500.0);
        ReservationResponseDTO reservationSaved = reservationService.createReservation(dto);

        assertNotNull(reservationSaved.id());
        assertEquals("car1", reservationSaved.carId());
        assertEquals("user1", reservationSaved.userId());
        assertEquals("CONFIRMED", reservationSaved.status());
        verify(kafkaTemplate).send(eq("reservations"), any(ReservationEvent.class));
    }

    @Test
    void shouldGetAllReservations() {
        List<Reservation> mockReservations = List.of(
                new Reservation("1", "car1", "user1", "PENDING"),
                new Reservation("2", "car2", "user2", "CONFIRMED"),
                new Reservation("3", "car3", "user3", "CANCELLED")
        );
        when(reservationRepository.findAll()).thenReturn(mockReservations);

        List<ReservationResponseDTO> reservationDTOList = reservationService.getAllReservations();

        assertEquals(3, reservationDTOList.size());
        assertEquals("car1", reservationDTOList.get(0).carId());
        assertEquals("CONFIRMED", reservationDTOList.get(1).status());
    }

    @Test
    void shouldUpdateReservation() {
        Reservation existingReservation = new Reservation("2", "car1", "user1", "PENDING");
        when(reservationRepository.findById("2")).thenReturn(Optional.of(existingReservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationDTO dto = new ReservationDTO("2", "car2", "user2", "CANCELLED", null, 3000);
        ReservationResponseDTO updatedReservation = reservationService.updateReservation("2", dto);

        assertEquals("car2", updatedReservation.carId());
        assertEquals("user2", updatedReservation.userId());
        assertEquals("CANCELLED", updatedReservation.status());
        verify(kafkaTemplate).send(eq("reservations"), any(ReservationEvent.class));
    }

    @Test
    void shouldGetReservationById() {
        Reservation reservation = new Reservation("10", "car1", "user1", "PENDING");
        when(reservationRepository.findById("10")).thenReturn(Optional.of(reservation));

        ReservationResponseDTO dto = reservationService.getReservationById("10");

        assertNotNull(dto.id());
        assertEquals("car1", dto.carId());
        assertEquals("user1", dto.userId());
        assertEquals("PENDING", dto.status());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenReservationNotExist() {
        when(reservationRepository.findById("10")).thenReturn(Optional.empty());
        when(reservationRepository.existsById("10")).thenReturn(false);

        assertThrows(ReservationNotFoundException.class, () -> reservationService.getReservationById("10"));
        assertThrows(ReservationNotFoundException.class, () -> reservationService.deleteReservation("10"));
    }
}