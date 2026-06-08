package com.waterx.station.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.waterx.station.dto.ReleaseRequest;
import com.waterx.station.dto.SlotInfoDTO;
import com.waterx.station.model.Station;
import com.waterx.station.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class StationService {

    private final BajieApiClient bajieApiClient;
    private final StationRepository stationRepository;

    private String resolveDeviceId(String inputId) {
        return stationRepository.findByQrCode(inputId)
                .map(Station::getStationId)
                .orElseGet(() -> {
                    String apiDeviceId = bajieApiClient.getDeviceIdFromQrCode(inputId);
                    return apiDeviceId != null ? apiDeviceId : inputId;
                });
    }

    public SlotInfoDTO getSlotInfo(String stationId, int slotNumber) {
        String deviceId = resolveDeviceId(stationId);
        // stationId is actually the deviceId in Bajie API
        JsonNode response = bajieApiClient.getCabinetInfo(deviceId);
        
        if (response == null || !response.has("code") || response.get("code").asInt() != 0 || !response.hasNonNull("data")) {
            log.error("Device not found or error from Bajie API for device: {}", deviceId);
            throw new RuntimeException("Could not find device information. Please check the QR code or try again.");
        }
        
        JsonNode data = response.get("data");
        if (data.has("posOnlineStatus") && !"online".equalsIgnoreCase(data.get("posOnlineStatus").asText())) {
            log.warn("Device {} is offline", deviceId);
            throw new RuntimeException("This station is currently offline. Please try another one.");
        }

        JsonNode priceStrategy = data.get("priceStrategy");
        JsonNode shop = data.get("shop");

        String status = "OCCUPIED"; // Default to OCCUPIED if no battery found in the slot
        boolean batteryFound = false;
        if (data.has("batteries")) {
            for (JsonNode battery : data.get("batteries")) {
                if (battery.get("slotNum").asInt() == slotNumber) {
                    batteryFound = true;
                    status = "AVAILABLE"; // Power bank exists in slot, ready to rent
                    break;
                }
            }
        }

        return SlotInfoDTO.builder()
                .stationId(deviceId)
                .stationName(shop != null && shop.has("name") ? shop.get("name").asText() : "Station " + deviceId)
                .location(shop != null && shop.has("address") ? shop.get("address").asText() : "Unknown Location")
                .slotNumber(slotNumber)
                .status(status)
                .pricePerHour(priceStrategy != null && priceStrategy.has("price") ? new BigDecimal(priceStrategy.get("price").asText()) : BigDecimal.ZERO)
                .depositAmount(priceStrategy != null && priceStrategy.has("depositAmount") ? new BigDecimal(priceStrategy.get("depositAmount").asText()) : BigDecimal.ZERO)
                .currency(priceStrategy != null && priceStrategy.has("currency") ? priceStrategy.get("currency").asText() : "INR")
                .build();
    }

    public SlotInfoDTO getBestSlotInfo(String stationId) {
        String deviceId = resolveDeviceId(stationId);
        JsonNode response = bajieApiClient.getCabinetInfo(deviceId);
        
        if (response == null || !response.has("code") || response.get("code").asInt() != 0 || !response.hasNonNull("data")) {
            log.error("Device not found or error from Bajie API for device: {}", deviceId);
            throw new RuntimeException("Could not find device information. Please check the QR code or try again.");
        }
        
        JsonNode data = response.get("data");
        if (data.has("posOnlineStatus") && !"online".equalsIgnoreCase(data.get("posOnlineStatus").asText())) {
            log.warn("Device {} is offline", deviceId);
            throw new RuntimeException("This station is currently offline. Please try another one.");
        }

        JsonNode priceStrategy = data.get("priceStrategy");
        JsonNode shop = data.get("shop");

        int bestSlotNumber = -1;
        int maxVol = -1;

        if (data.has("batteries")) {
            for (JsonNode battery : data.get("batteries")) {
                int vol = battery.has("vol") ? battery.get("vol").asInt() : 0;
                int slotNum = battery.has("slotNum") ? battery.get("slotNum").asInt() : -1;
                if (vol > maxVol && slotNum != -1) {
                    maxVol = vol;
                    bestSlotNumber = slotNum;
                }
            }
        }

        if (bestSlotNumber == -1) {
            log.error("No available power banks found for device: {}", deviceId);
            throw new RuntimeException("No power banks available at this station.");
        }

        return SlotInfoDTO.builder()
                .stationId(deviceId)
                .stationName(shop != null && shop.has("name") ? shop.get("name").asText() : "Station " + deviceId)
                .location(shop != null && shop.has("address") ? shop.get("address").asText() : "Unknown Location")
                .slotNumber(bestSlotNumber)
                .status("AVAILABLE")
                .pricePerHour(priceStrategy != null && priceStrategy.has("price") ? new BigDecimal(priceStrategy.get("price").asText()) : BigDecimal.ZERO)
                .depositAmount(priceStrategy != null && priceStrategy.has("depositAmount") ? new BigDecimal(priceStrategy.get("depositAmount").asText()) : BigDecimal.ZERO)
                .currency(priceStrategy != null && priceStrategy.has("currency") ? priceStrategy.get("currency").asText() : "INR")
                .build();
    }

    public boolean releaseSlot(ReleaseRequest request) {
        log.info("Triggering Bajie API release for device: {}, slot: {}, rentOrderId: {}", 
                 request.getStationId(), request.getSlotNumber(), request.getBajieTradeNo());
        
        return bajieApiClient.ejectByRent(request.getStationId(), request.getBajieTradeNo(), request.getSlotNumber());
    }
}
