package com.waterx.payment.controller;

import com.razorpay.RazorpayException;
import com.waterx.payment.dto.*;
import com.waterx.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.waterx.payment.dto.RefundRequest;
import com.waterx.payment.dto.RefundResponse;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            CreateOrderResponse response = paymentService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (RazorpayException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyPaymentResponse> verifyPayment(@RequestBody VerifyPaymentRequest request) {
        try {
            VerifyPaymentResponse response = paymentService.verifyPayment(request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<RefundResponse> processRefund(@RequestBody RefundRequest request) {
        try {
            RefundResponse response = paymentService.processRefund(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
