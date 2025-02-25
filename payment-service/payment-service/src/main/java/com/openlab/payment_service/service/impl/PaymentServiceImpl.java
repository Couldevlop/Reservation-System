package com.openlab.payment_service.service.impl;

import com.openlab.payment_service.exception.PaymentNotFoundException;
import com.openlab.payment_service.model.Payment;
import com.openlab.payment_service.model.PaymentDTO;
import com.openlab.payment_service.repository.PaymentRepository;
import com.openlab.payment_service.service.PaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {

        Payment payment = paymentDTO.toEntity();
        boolean isValid = validateCardNumber(paymentDTO.cardNumber());
        payment.setId(UUID.randomUUID().toString());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setSuccessful(isValid);

        Payment paymentSaved = paymentRepository.save(payment);

        return PaymentDTO.fromEntity(paymentSaved);
    }

    @Override
    public PaymentDTO getPaymentById(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payement not found with id: " +id));
        return PaymentDTO.fromEntity(payment);
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream().map(PaymentDTO::fromEntity).toList();
    }

    @Override
    public PaymentDTO updatePayment(String id, PaymentDTO paymentDTO) {
       Payment payment = paymentRepository.findById(id)
               .orElseThrow(()-> new PaymentNotFoundException("Payement not found with id: " +id));
        payment.setCreatedAt(LocalDateTime.now());
        payment.setAmount(paymentDTO.amount());
        payment.setSuccessful(payment.isSuccessful());
        if(validateCardNumber(paymentDTO.cardNumber())){
            payment.setCardNumber(paymentDTO.cardNumber());
        }else {
            throw new IllegalArgumentException("Error with cardNumber");
        }
       Payment paymentSaved = paymentRepository.save(payment);
        return PaymentDTO.fromEntity(paymentSaved);
    }

    @Override
    public void deletePayment(String id) {
   if(!paymentRepository.existsById(id)){
      throw  new PaymentNotFoundException("Payement not found with id: " +id);
   }
      paymentRepository.deleteById(id);
    }


    private boolean validateCardNumber(String cardNumber) {
        boolean isValid = cardNumber != null && cardNumber.startsWith("4") && cardNumber.length() == 16;
        System.out.println("Card validation: " + cardNumber + " -> " + isValid);
        return isValid;
    }
}
