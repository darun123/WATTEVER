package com.waterx.payment.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class RefundResponse {
    private String refundId;
    private String paymentId;
    private BigDecimal amount;
    private String status;
}
