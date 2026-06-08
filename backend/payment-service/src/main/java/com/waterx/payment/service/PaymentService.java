package com.waterx.payment.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.waterx.payment.dto.*;
import com.waterx.payment.model.PaymentRecord;
import com.waterx.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    @Value("${services.rental-service-url}")
    private String rentalServiceUrl;

    @Value("${services.station-service-url}")
    private String stationServiceUrl;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) throws RazorpayException {
        // Amount in paise (1 INR = 100 paise)
        long amountInPaise = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", request.getCurrency());
        orderRequest.put("receipt", "rental_" + request.getRentalId().toString().substring(0, 8));
        orderRequest.put("payment_capture", 1);

        Order order = razorpayClient.orders.create(orderRequest);
        String orderId = order.get("id");

        // Save payment record
        PaymentRecord record = new PaymentRecord();
        record.setRentalId(request.getRentalId());
        record.setStationId(request.getStationId());
        record.setSlotNumber(request.getSlotNumber());
        record.setRazorpayOrderId(orderId);
        record.setAmount(request.getAmount());
        record.setCurrency(request.getCurrency());
        record.setStatus(PaymentRecord.PaymentStatus.CREATED);
        record.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(record);

        log.info("Razorpay order created: {} for rental: {}", orderId, request.getRentalId());

        return CreateOrderResponse.builder()
                .orderId(orderId)
                .rentalId(request.getRentalId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .keyId(razorpayKeyId)
                .stationId(request.getStationId())
                .slotNumber(request.getSlotNumber())
                .build();
    }

    @Transactional
    public VerifyPaymentResponse verifyPayment(VerifyPaymentRequest request) {
        try {
            // 1. Verify Razorpay signature
            String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
            boolean isValid = Utils.verifySignature(payload, request.getRazorpaySignature(), razorpayKeySecret);

            if (!isValid) {
                log.warn("Invalid Razorpay signature for order: {}", request.getRazorpayOrderId());
                markPaymentFailed(request);
                return VerifyPaymentResponse.builder()
                        .success(false)
                        .message("Payment verification failed: invalid signature")
                        .rentalId(request.getRentalId())
                        .released(false)
                        .build();
            }

            // 2. Update payment record
            PaymentRecord record = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElse(new PaymentRecord());
            record.setRazorpayPaymentId(request.getRazorpayPaymentId());
            record.setRazorpaySignature(request.getRazorpaySignature());
            record.setStatus(PaymentRecord.PaymentStatus.SUCCESS);
            record.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(record);

            // 3. Complete the rental in rental-service
            String bajieTradeNo = null;
            try {
                String completeUrl = rentalServiceUrl + "/api/rentals/" + request.getRentalId() + "/complete";
                Map<String, String> completeBody = Map.of(
                        "razorpayOrderId", request.getRazorpayOrderId(),
                        "razorpayPaymentId", request.getRazorpayPaymentId()
                );
                ResponseEntity<Map> completeRes = restTemplate.postForEntity(completeUrl, completeBody, Map.class);
                bajieTradeNo = (String) completeRes.getBody().get("bajieTradeNo");
            } catch (Exception e) {
                log.error("Failed to call complete API on rental-service: {}", e.getMessage());
            }

            // 4. Trigger physical power bank release via station-service
            boolean released = false;
            try {
                String releaseUrl = stationServiceUrl + "/api/stations/" + request.getStationId()
                        + "/slot/" + request.getSlotNumber() + "/release";
                Map<String, Object> releaseBody = Map.of(
                        "rentalId", request.getRentalId().toString(),
                        "stationId", request.getStationId(),
                        "slotNumber", request.getSlotNumber(),
                        "bajieTradeNo", bajieTradeNo != null ? bajieTradeNo : ""
                );
                ResponseEntity<Map> releaseResponse = restTemplate.postForEntity(
                        releaseUrl, releaseBody, Map.class);
                released = releaseResponse.getStatusCode().is2xxSuccessful();

                // 5. Mark rental as released
                if (released) {
                    String releasedUrl = rentalServiceUrl + "/api/rentals/" + request.getRentalId() + "/released";
                    restTemplate.postForEntity(releasedUrl, null, Object.class);
                }
            } catch (Exception e) {
                log.error("Failed to trigger release for station: {}, slot: {}: {}",
                        request.getStationId(), request.getSlotNumber(), e.getMessage());
            }

            log.info("Payment verified and power bank released. Rental: {}, Payment: {}",
                    request.getRentalId(), request.getRazorpayPaymentId());

            return VerifyPaymentResponse.builder()
                    .success(true)
                    .message(released ? "Payment successful! Power bank released." : "Payment successful! Release initiated.")
                    .rentalId(request.getRentalId())
                    .paymentId(request.getRazorpayPaymentId())
                    .released(released)
                    .build();

        } catch (RazorpayException e) {
            log.error("Razorpay verification error: {}", e.getMessage());
            markPaymentFailed(request);
            return VerifyPaymentResponse.builder()
                    .success(false)
                    .message("Payment verification error: " + e.getMessage())
                    .rentalId(request.getRentalId())
                    .released(false)
                    .build();
        }
    }

    private void markPaymentFailed(VerifyPaymentRequest request) {
        try {
            PaymentRecord record = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                    .orElse(new PaymentRecord());
            record.setStatus(PaymentRecord.PaymentStatus.FAILED);
            record.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(record);

            String failedUrl = rentalServiceUrl + "/api/rentals/" + request.getRentalId() + "/failed";
            restTemplate.postForEntity(failedUrl, null, Object.class);
        } catch (Exception e) {
            log.error("Failed to mark payment as failed: {}", e.getMessage());
        }
    }

    @Transactional
    public RefundResponse processRefund(RefundRequest request) {
        try {
            long amountInPaise = request.getAmount().multiply(BigDecimal.valueOf(100)).longValue();
            JSONObject refundReq = new JSONObject();
            refundReq.put("amount", amountInPaise);
            
            com.razorpay.Refund refund = razorpayClient.payments.refund(request.getPaymentId(), refundReq);
            String refundId = refund.get("id");
            String status = refund.get("status");

            log.info("Processed refund {} for payment {}. Status: {}", refundId, request.getPaymentId(), status);

            return RefundResponse.builder()
                    .refundId(refundId)
                    .paymentId(request.getPaymentId())
                    .amount(request.getAmount())
                    .status(status)
                    .build();
        } catch (RazorpayException e) {
            log.error("Failed to process refund for payment {}: {}", request.getPaymentId(), e.getMessage());
            throw new RuntimeException("Refund processing failed", e);
        }
    }
}
