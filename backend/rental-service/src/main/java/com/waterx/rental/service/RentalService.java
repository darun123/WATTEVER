package com.waterx.rental.service;

import com.waterx.rental.dto.CompleteRentalRequest;
import com.waterx.rental.dto.InitiateRentalRequest;
import com.waterx.rental.dto.RentalResponse;
import com.waterx.rental.model.RentalSession;
import com.waterx.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RentalService {

    private final RentalRepository rentalRepository;
    private final BajieApiClient bajieApiClient;
    private final org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

    @Transactional
    public RentalResponse initiateRental(InitiateRentalRequest request) {
        RentalSession session = new RentalSession();
        session.setStationId(request.getStationId());
        session.setSlotNumber(request.getSlotNumber());
        session.setUserPhone(request.getUserPhone());
        session.setUserEmail(request.getUserEmail());
        session.setUserName(request.getUserName());
        session.setAmount(request.getAmount());
        session.setCurrency(request.getCurrency());
        session.setStatus(RentalSession.RentalStatus.PENDING);
        session.setCreatedAt(LocalDateTime.now());

        RentalSession saved = rentalRepository.save(session);
        log.info("Rental session created: {}", saved.getId());

        return RentalResponse.builder()
                .rentalId(saved.getId())
                .stationId(saved.getStationId())
                .slotNumber(saved.getSlotNumber())
                .status(saved.getStatus().name())
                .amount(saved.getAmount())
                .currency(saved.getCurrency())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    public RentalResponse getRental(UUID rentalId) {
        RentalSession session = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found: " + rentalId));

        return mapToResponse(session);
    }

    @Transactional
    public RentalResponse completeRental(UUID rentalId, CompleteRentalRequest request) {
        RentalSession session = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental session not found"));

        if (session.getStatus() != RentalSession.RentalStatus.PENDING) {
            throw new RuntimeException("Rental is not in PENDING state");
        }

        session.setStatus(RentalSession.RentalStatus.PAID);
        session.setRazorpayOrderId(request.getRazorpayOrderId());
        session.setRazorpayPaymentId(request.getRazorpayPaymentId());
        session.setStartTime(LocalDateTime.now());

        // Create Bajie order
        String callbackUrl = "https://rich-buckets-yell.loca.lt/api/rentals/bajie-webhook";
        String tradeNo = bajieApiClient.createRentOrder(session.getStationId(), callbackUrl, session.getSlotNumber());
        if (tradeNo != null) {
            session.setBajieTradeNo(tradeNo);
        } else {
            log.warn("Could not create Bajie order for rental {}", rentalId);
        }

        RentalSession saved = rentalRepository.save(session);
        log.info("Rental {} completed and PAID via Razorpay. Bajie Trade No: {}", rentalId, tradeNo);
        
        return mapToResponse(saved);
    }

    private RentalResponse mapToResponse(RentalSession session) {
        return RentalResponse.builder()
                .rentalId(session.getId())
                .stationId(session.getStationId())
                .slotNumber(session.getSlotNumber())
                .status(session.getStatus().name())
                .amount(session.getAmount())
                .currency(session.getCurrency())
                .createdAt(session.getCreatedAt())
                .razorpayOrderId(session.getRazorpayOrderId())
                .bajieTradeNo(session.getBajieTradeNo())
                .build();
    }

    @Transactional
    public void markReleased(UUID rentalId) {
        RentalSession session = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found: " + rentalId));
        session.setStatus(RentalSession.RentalStatus.RELEASED);
        rentalRepository.save(session);
        log.info("Rental marked as RELEASED: {}", rentalId);
    }

    @Transactional
    public void markFailed(UUID rentalId) {
        RentalSession session = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found: " + rentalId));
        session.setStatus(RentalSession.RentalStatus.FAILED);
        rentalRepository.save(session);
    }

    @Transactional
    public void handleReturn(com.waterx.rental.dto.BajieWebhookRequest request) {
        RentalSession session = rentalRepository.findByBajieTradeNo(request.getTradeNo())
                .orElseThrow(() -> new RuntimeException("Rental not found for trade no: " + request.getTradeNo()));

        if (session.getStatus() == RentalSession.RentalStatus.COMPLETED) {
            log.info("Rental already completed: {}", session.getId());
            return;
        }

        session.setStatus(RentalSession.RentalStatus.COMPLETED);
        session.setEndTime(LocalDateTime.now());
        session.setSettlementAmount(request.getSettlementAmount());

        java.math.BigDecimal refundAmount = session.getAmount().subtract(request.getSettlementAmount());
        if (refundAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
            session.setRefundAmount(refundAmount);
            // Process Refund
            try {
                java.util.Map<String, Object> refundReq = new java.util.HashMap<>();
                refundReq.put("paymentId", session.getRazorpayPaymentId());
                refundReq.put("amount", refundAmount);
                
                restTemplate.postForEntity("http://localhost:8083/api/payments/refund", refundReq, Object.class);
                log.info("Successfully processed refund of {} for payment {}", refundAmount, session.getRazorpayPaymentId());
            } catch (Exception e) {
                log.error("Failed to process refund: {}", e.getMessage());
            }
        }

        rentalRepository.save(session);
        log.info("Rental {} marked as COMPLETED. Settlement: {}", session.getId(), request.getSettlementAmount());
    }
}
