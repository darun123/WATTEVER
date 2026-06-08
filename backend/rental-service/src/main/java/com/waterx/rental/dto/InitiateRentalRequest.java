package com.waterx.rental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class InitiateRentalRequest {
    @NotBlank
    private String stationId;

    @NotNull
    private Integer slotNumber;

    private String userPhone;
    private String userEmail;
    private String userName;

    @NotNull
    private BigDecimal amount;

    private String currency = "INR";
}
