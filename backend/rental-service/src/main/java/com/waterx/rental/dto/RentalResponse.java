package com.waterx.rental.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RentalResponse {
    private UUID rentalId;
    private String stationId;
    private int slotNumber;
    private String status;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime createdAt;
    private String razorpayOrderId;
    private String bajieTradeNo;
}
