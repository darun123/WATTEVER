package com.waterx.station.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@Builder
public class SlotInfoDTO {
    private String stationId;
    private String stationName;
    private String location;
    private int slotNumber;
    private String status;
    private BigDecimal pricePerHour;
    private BigDecimal depositAmount;
    private String currency;
}
