package com.waterx.station.controller;

import com.waterx.station.dto.ReleaseRequest;
import com.waterx.station.dto.SlotInfoDTO;
import com.waterx.station.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @GetMapping("/{stationId}/slot/{slotNumber}")
    public ResponseEntity<?> getSlotInfo(
            @PathVariable String stationId,
            @PathVariable int slotNumber) {
        try {
            SlotInfoDTO slotInfo = stationService.getSlotInfo(stationId, slotNumber);
            return ResponseEntity.ok(slotInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{stationId}/best-slot")
    public ResponseEntity<?> getBestSlotInfo(@PathVariable String stationId) {
        try {
            SlotInfoDTO slotInfo = stationService.getBestSlotInfo(stationId);
            return ResponseEntity.ok(slotInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{stationId}/slot/{slotNumber}/release")
    public ResponseEntity<Map<String, Object>> releaseSlot(
            @PathVariable String stationId,
            @PathVariable int slotNumber,
            @RequestBody ReleaseRequest request) {
        request.setStationId(stationId);
        request.setSlotNumber(slotNumber);
        boolean success = stationService.releaseSlot(request);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Power bank released successfully"));
        } else {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "message", "Failed to release power bank"));
        }
    }
}
