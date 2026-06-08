package com.waterx.station.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "stations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Station {

    @Id
    @Column(name = "station_id")
    private String stationId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "total_slots", nullable = false)
    private int totalSlots;

    @Column(name = "price_per_hour", nullable = false)
    private BigDecimal pricePerHour;

    @Column(name = "deposit_amount")
    private BigDecimal depositAmount;

    @Column(name = "currency", nullable = false)
    private String currency = "INR";

    @Column(name = "release_api_url")
    private String releaseApiUrl;

    @Column(name = "release_api_key")
    private String releaseApiKey;

    @Column(name = "qr_code", unique = true)
    private String qrCode;
}
