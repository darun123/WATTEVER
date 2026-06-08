package com.waterx.rental.dto;

import lombok.Data;

@Data
public class CompleteRentalRequest {
    private String razorpayOrderId;
    private String razorpayPaymentId;
}
