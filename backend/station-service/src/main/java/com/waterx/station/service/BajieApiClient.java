package com.waterx.station.service;

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

    public JsonNode getCabinetInfo(String deviceId) {
        String url = baseUrl + "/rent/cabinet/query?deviceId=" + deviceId;
        HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Exception calling Bajie getCabinetInfo: {}", e.getMessage());
            return null;
        }
    }

    public String getDeviceIdFromQrCode(String qrCode) {
        String url = baseUrl + "/cabinet/detail/" + qrCode;
        HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode body = response.getBody();
            if (body != null && body.has("code") && body.get("code").asInt() == 0 && body.hasNonNull("data")) {
                JsonNode data = body.get("data");
                if (data.has("deviceId")) {
                    return data.get("deviceId").asText();
                } else if (data.has("pcabinetid")) {
                    return data.get("pcabinetid").asText();
                } else if (data.has("pCabinetid")) {
                    return data.get("pCabinetid").asText();
                }
            }
            log.warn("Could not find deviceId from QR code. Response: {}", body);
            return null;
        } catch (Exception e) {
            log.error("Exception calling Bajie getDeviceIdFromQrCode: {}", e.getMessage());
            return null;
        }
    }

    public boolean ejectByRent(String deviceId, String rentOrderId, int slotNum) {
        String url = baseUrl + "/cabinet/ejectByRent?cabinetid=" + deviceId + "&rentOrderId=" + rentOrderId + "&slotNum=" + slotNum + "&slot=" + slotNum;
        HttpEntity<String> entity = new HttpEntity<>(createAuthHeaders());
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, entity, JsonNode.class);
            JsonNode body = response.getBody();
            log.info("Bajie eject response: {}", body);
            if (body != null && body.has("code") && body.get("code").asInt() == 0) {
                return true;
            }
            log.error("Failed to eject. Response: {}", body);
            return false;
        } catch (Exception e) {
            log.error("Exception calling Bajie ejectByRent API: {}", e.getMessage());
            return false;
        }
    }
}
