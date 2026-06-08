package com.waterx.rental.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BajieWebhookRequest {
    private String tradeNo; // The rentOrderId or bajieTradeNo
    private BigDecimal settlementAmount;
    private String returnTime;
    private String status;
}
