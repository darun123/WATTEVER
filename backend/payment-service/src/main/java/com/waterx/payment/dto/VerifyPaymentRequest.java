package com.waterx.payment.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class VerifyPaymentRequest {
    private UUID rentalId;
    private String stationId;
    private int slotNumber;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
