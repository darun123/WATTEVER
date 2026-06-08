package com.waterx.rental.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class BajieApiClient {
    private final RestTemplate restTemplate;

    @Value("${bajie.base-url:https://developer.chargenow.top/cdb-open-api/v1}")
    private String baseUrl;

    @Value("${bajie.username:YOUR_USERNAME}")
    private String username;

    @Value("${bajie.password:YOUR_PASSWORD}")
    private String password;

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + new String(encodedAuth));
        return headers;
    }

    public String createRentOrder(String deviceId, String callbackURL, int slotNum) {
        String url = baseUrl + "/rent/order/create?deviceId=" + deviceId + "&callbackURL=" + callbackURL + "&slotNum=" + slotNum;
        HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);
            JsonNode body = response.getBody();
            if (body != null && body.has("code") && body.get("code").asInt() == 0) {
                return body.get("data").get("tradeNo").asText();
            }
            log.error("Failed to create Bajie order. Response: {}", body);
            return null;
        } catch (Exception e) {
            log.error("Exception calling Bajie createRentOrder API: {}", e.getMessage());
            return null;
        }
    }

    public JsonNode queryOrder(String tradeNo) {
        String url = baseUrl + "/rent/order/query?tradeNo=" + tradeNo;
        HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Exception calling Bajie queryOrder API: {}", e.getMessage());
            return null;
        }
    }
}
