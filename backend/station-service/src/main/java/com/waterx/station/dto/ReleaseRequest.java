package com.waterx.station.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseRequest {
    private String rentalId;
    private String stationId;
    private int slotNumber;
    private String bajieTradeNo;
}
