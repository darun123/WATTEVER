package com.waterx.payment.dto;

import lombok.Data;
import lombok.Builder;
import java.util.UUID;

@Data
@Builder
public class VerifyPaymentResponse {
    private boolean success;
    private String message;
    private UUID rentalId;
    private String paymentId;
    private boolean released;
}
