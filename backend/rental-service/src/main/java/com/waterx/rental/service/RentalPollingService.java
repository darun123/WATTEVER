package com.waterx.rental.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.waterx.rental.model.RentalSession;
import com.waterx.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalPollingService {

    private final RentalRepository rentalRepository;
    private final BajieApiClient bajieApiClient;
    private final RentalService rentalService;

    // Run every 1 minute
    @Scheduled(fixedRate = 60000)
    public void pollActiveRentals() {
        log.info("Starting scheduled polling for active rentals...");
        
        // Find rentals that are PAID or RELEASED but not yet COMPLETED
        List<RentalSession> activeRentals = rentalRepository.findAll().stream()
                .filter(r -> r.getStatus() == RentalSession.RentalStatus.PAID || r.getStatus() == RentalSession.RentalStatus.RELEASED)
                .toList();

        for (RentalSession rental : activeRentals) {
            if (rental.getBajieTradeNo() == null || rental.getBajieTradeNo().isBlank()) {
                continue;
            }

            try {
                // Poll the Bajie API for the status of this order
                log.info("Polling status for Bajie Trade No: {}", rental.getBajieTradeNo());
                JsonNode response = bajieApiClient.queryOrder(rental.getBajieTradeNo());

                if (response != null && response.has("code") && response.get("code").asInt() == 0) {
                    JsonNode data = response.get("data");
                    if (data != null) {
                        // We check for a status indicating the power bank was returned.
                        // Assuming "status" = "COMPLETED" or "RETURNED" or similar.
                        String status = data.has("status") ? data.get("status").asText() : "";
                        
                        if ("COMPLETED".equalsIgnoreCase(status) || "RETURNED".equalsIgnoreCase(status)) {
                            BigDecimal settlementAmount = data.has("settlementAmount") 
                                    ? new BigDecimal(data.get("settlementAmount").asText()) 
                                    : BigDecimal.ZERO;
                            
                            // Simulate the webhook payload to reuse existing logic
                            com.waterx.rental.dto.BajieWebhookRequest mockWebhook = new com.waterx.rental.dto.BajieWebhookRequest();
                            mockWebhook.setTradeNo(rental.getBajieTradeNo());
                            mockWebhook.setSettlementAmount(settlementAmount);
                            
                            log.info("Polling detected completed rental. Processing return for trade: {}", rental.getBajieTradeNo());
                            rentalService.handleReturn(mockWebhook);
                        }
                    }
                } else {
                    log.debug("Polling API returned non-zero code or null for trade: {}. Response: {}", rental.getBajieTradeNo(), response);
                }
            } catch (Exception e) {
                log.error("Error polling order status for trade: {}", rental.getBajieTradeNo(), e);
            }
        }
    }
}
