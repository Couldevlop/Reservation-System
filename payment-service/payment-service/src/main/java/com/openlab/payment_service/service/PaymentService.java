package com.openlab.payment_service.service;

import com.openlab.payment_service.model.PaymentDTO;

import java.util.List;

public interface PaymentService {
    public PaymentDTO createPayment(PaymentDTO paymentDTO);
    public PaymentDTO getPaymentById(String id);
    public List<PaymentDTO> getAllPayments();
    public PaymentDTO updatePayment(String id, PaymentDTO paymentDTO);
    public void deletePayment(String id);

}
