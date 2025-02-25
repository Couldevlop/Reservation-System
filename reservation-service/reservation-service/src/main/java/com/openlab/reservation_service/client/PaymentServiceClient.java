package com.openlab.reservation_service.client;


import com.openlab.reservation_service.model.PaymentRequest;
import com.openlab.reservation_service.model.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/api/v1/payments")
    PaymentResponse createPayment(@RequestBody @Validated PaymentRequest paymentRequest);
}
