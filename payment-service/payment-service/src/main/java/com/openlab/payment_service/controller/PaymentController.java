package com.openlab.payment_service.controller;

import com.openlab.payment_service.model.PaymentDTO;
import com.openlab.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping
    @ResponseStatus( HttpStatus.CREATED)
    public PaymentDTO createPayment(@RequestBody @Validated PaymentDTO dto){
        return paymentService.createPayment(dto);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentDTO> getAllPayments(){
        return  paymentService.getAllPayments();
    }



    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDTO getPaymentById(@PathVariable String id){
        return paymentService.getPaymentById(id);
    }



    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDTO updatePayment(@PathVariable String id, @RequestBody @Validated PaymentDTO dto){
        return  paymentService.updatePayment(id,dto);
    }


    @DeleteMapping
    public ResponseEntity<String> deletePayment(@PathVariable String id){
        paymentService.deletePayment(id);
        return ResponseEntity.ok("Payment with id:" + id + "is deleted successfully");
    }
}
