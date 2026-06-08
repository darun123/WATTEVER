package com.waterx.payment.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RefundRequest {
    private String paymentId;
    private BigDecimal amount;
}
