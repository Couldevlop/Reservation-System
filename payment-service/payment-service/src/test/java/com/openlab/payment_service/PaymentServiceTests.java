package com.openlab.payment_service;

import com.openlab.payment_service.exception.PaymentNotFoundException;
import com.openlab.payment_service.model.Payment;
import com.openlab.payment_service.model.PaymentDTO;
import com.openlab.payment_service.repository.PaymentRepository;
import com.openlab.payment_service.service.PaymentService;
import com.openlab.payment_service.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class PaymentServiceTests {
    @Mock
    private PaymentRepository  paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    }


    @Test
    void shouldCreatePayment(){
        PaymentDTO dto = new PaymentDTO(null, "1",9000,"4123456789456123",null,true);

        PaymentDTO paymentSaved = paymentService.createPayment(dto);

        assertNotNull(paymentSaved.id());
        assertEquals("1", paymentSaved.reservationId());
        assertEquals(9000.0, paymentSaved.amount());
        assertEquals("4123456789456123", paymentSaved.cardNumber());
        assertTrue(paymentSaved.successful());

    }

    @Test
    void shouldThrowExceptionWhenCardNumberNotCorrect(){
        PaymentDTO dtoInvalidPrefix = new PaymentDTO(null, "1",9000,"5123456789456123",null,true);
        PaymentDTO dtoInvalidLength = new PaymentDTO(null, "2",9000,"412345678945612",null,true);


        assertFalse(paymentService.createPayment(dtoInvalidPrefix).successful(),"Payment should fail for invalid prefix");
        assertFalse(paymentService.createPayment(dtoInvalidLength).successful(), "Payment should fail for invalid length");

    }

    @Test
    void shouldGetPaymentById(){
        Payment payment = new Payment("1", "1",9000,"4123456789456123", LocalDateTime.now(),true);
        when(paymentRepository.findById("1")).thenReturn(Optional.of(payment));

        PaymentDTO dto = paymentService.getPaymentById("1");

        assertNotNull(dto);
        assertNotNull(dto.id());
        assertEquals("1", dto.reservationId());
        assertEquals(9000.0, dto.amount());
        assertEquals("4123456789456123", dto.cardNumber());
        assertEquals("1", dto.id());
        assertTrue(dto.successful());
    }


    @Test
    void shouldThrowNotFoundExceptionPaymentNotExist(){
        when(paymentRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById("1"));
    }


    @Test
    void shoulGetAllPaiements(){
        List<Payment> mockPayments = List.of(
                new Payment(null, "1",9000,"4123456789456123",null,true),
                new Payment(null, "2",20000,"4123456789450028",null,true)
        );

        when(paymentRepository.findAll()).thenReturn(mockPayments);

        List<PaymentDTO> paymentDTOList = paymentService.getAllPayments();


        assertEquals(2, paymentDTOList.size());
        assertEquals("1", paymentDTOList.getFirst().reservationId());
        assertEquals(20000.0, paymentDTOList.get(1).amount());
    }


    @Test
    void shouldUpdatePayment(){
        Payment existingPayment = new Payment("1", "1", 9000.0, "4123456789456123", LocalDateTime.now(), true);
        when(paymentRepository.findById("1")).thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentDTO dto = new PaymentDTO("1", "1", 10000.0, "4123456789456789", null, true);

        PaymentDTO upPaymentDTO = paymentService.updatePayment("1", dto);

        assertEquals("1", upPaymentDTO.id());
        assertEquals(10000.0, upPaymentDTO.amount());
        assertEquals("4123456789456789", upPaymentDTO.cardNumber());
        assertTrue(upPaymentDTO.successful());
        verify(paymentRepository, times(1)).save(any(Payment.class));

    }


    @Test
    void shouldThrowExceptionWhenUpdatingWithInvalidCardNumber() {

        Payment existingPayment = new Payment("1", "1", 9000.0, "4123456789456123", LocalDateTime.now(), true);
        when(paymentRepository.findById("1")).thenReturn(Optional.of(existingPayment));

        PaymentDTO dto = new PaymentDTO("1", "1", 10000.0, "5123456789456123", null, true); // Invalid card number


        assertThrows(IllegalArgumentException.class, () -> paymentService.updatePayment("1", dto));
        verify(paymentRepository, never()).save(any(Payment.class));
    }


    @Test
    void shouldDeletePayment() {

        when(paymentRepository.existsById("1")).thenReturn(true);


        paymentService.deletePayment("1");


        verify(paymentRepository, times(1)).deleteById("1");
    }


    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNoExistentPayment() {

        when(paymentRepository.existsById("1")).thenReturn(false);


        assertThrows(PaymentNotFoundException.class, () -> paymentService.deletePayment("1"));
        verify(paymentRepository, never()).deleteById("1");
    }

}
