package com.waterx.rental.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/webhooks")
public class ReturnWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(ReturnWebhookController.class);
    
    // In production, load this from your application.properties or environment variables!
    private static final String WEBHOOK_SECRET = "your_provider_provided_secret_key";

    @PostMapping("/returns")
    public ResponseEntity<String> handleReturnWebhook(
            @RequestHeader(value = "X-Provider-Signature", required = false) String signature,
            @RequestBody String payload) {
            
        logger.info("Received webhook payload: {}", payload);

        // 1. Verify the signature (Security measure)
        // Note: The exact header name ("X-Provider-Signature") and hashing algorithm
        // will depend on your specific API provider's documentation.
        if (signature == null || !isValidSignature(payload, signature, WEBHOOK_SECRET)) {
            logger.warn("Webhook signature verification failed!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        // 2. Parse and Process the Payload
        try {
            // Here you would use an ObjectMapper (like Jackson) to convert the JSON payload 
            // string into a Java Data Transfer Object (DTO).
            // Example:
            // WebhookEvent event = objectMapper.readValue(payload, WebhookEvent.class);
            
            // if ("return.completed".equals(event.getType())) {
            //     // Handle successful return logic (e.g. update DB, trigger payment refund)
            // }

            // 3. Acknowledge Receipt
            // Providers usually expect a 200 OK response quickly to know you received it.
            return ResponseEntity.ok("Webhook received successfully");

        } catch (Exception e) {
            logger.error("Error processing webhook payload", e);
            // Return a 500 or 400 depending on if it's a parsing error or internal error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing payload");
        }
    }

    // Helper method to verify HMAC SHA256 signatures (Common among providers like Stripe, GitHub, etc.)
    private boolean isValidSignature(String payload, String expectedSignature, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString().equalsIgnoreCase(expectedSignature);
        } catch (Exception e) {
            logger.error("Error calculating HMAC signature", e);
            return false;
        }
    }
}
