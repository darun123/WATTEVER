package com.waterx.rental.controller;

import com.waterx.rental.dto.CompleteRentalRequest;
import com.waterx.rental.dto.InitiateRentalRequest;
import com.waterx.rental.dto.RentalResponse;
import com.waterx.rental.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("/initiate")
    public ResponseEntity<RentalResponse> initiateRental(@Valid @RequestBody InitiateRentalRequest request) {
        RentalResponse response = rentalService.initiateRental(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{rentalId}")
    public ResponseEntity<RentalResponse> getRental(@PathVariable UUID rentalId) {
        try {
            RentalResponse response = rentalService.getRental(rentalId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{rentalId}/complete")
    public ResponseEntity<RentalResponse> completeRental(
            @PathVariable UUID rentalId,
            @RequestBody CompleteRentalRequest request) {
        try {
            RentalResponse response = rentalService.completeRental(rentalId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{rentalId}/released")
    public ResponseEntity<Map<String, String>> markReleased(@PathVariable UUID rentalId) {
        rentalService.markReleased(rentalId);
        return ResponseEntity.ok(Map.of("status", "RELEASED"));
    }

    @PostMapping("/{rentalId}/failed")
    public ResponseEntity<Map<String, String>> markFailed(@PathVariable UUID rentalId) {
        rentalService.markFailed(rentalId);
        return ResponseEntity.ok(Map.of("status", "FAILED"));
    }

    @PostMapping(value = "/bajie-webhook", consumes = {org.springframework.http.MediaType.APPLICATION_JSON_VALUE, org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<Map<String, String>> handleBajieWebhook(com.waterx.rental.dto.BajieWebhookRequest request) {
        rentalService.handleReturn(request);
        return ResponseEntity.ok(Map.of("status", "SUCCESS"));
    }
}
