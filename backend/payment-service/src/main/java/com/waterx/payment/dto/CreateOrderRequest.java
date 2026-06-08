package com.waterx.payment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateOrderRequest {
    private UUID rentalId;
    private String stationId;
    private int slotNumber;
    private BigDecimal amount;
    private String currency = "INR";
}
