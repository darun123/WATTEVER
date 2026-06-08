package com.waterx.payment.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CreateOrderResponse {
    private String orderId;
    private UUID rentalId;
    private BigDecimal amount;
    private String currency;
    private String keyId;
    private String stationId;
    private int slotNumber;
}
