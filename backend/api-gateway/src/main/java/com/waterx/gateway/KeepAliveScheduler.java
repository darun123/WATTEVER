package com.waterx.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Keeps all Render free-tier services alive by pinging their health endpoints
 * every 10 minutes. Prevents the 50-90s cold start delay that causes
 * the frontend to fail to connect to the backend.
 */
@Component
@EnableScheduling
public class KeepAliveScheduler {

    private static final Logger log = LoggerFactory.getLogger(KeepAliveScheduler.class);

    private final WebClient webClient = WebClient.builder().build();

    private static final List<String> SERVICE_HEALTH_URLS = List.of(
        "https://wattever-1.onrender.com/actuator/health",   // rental-service
        "https://wattever-2.onrender.com/actuator/health",   // station-service
        "https://wattever-3.onrender.com/actuator/health"    // payment-service
    );

    // Runs every 10 minutes (600,000 ms). Adjust if needed.
    @Scheduled(fixedDelay = 600_000, initialDelay = 30_000)
    public void pingServices() {
        log.info("Keep-alive: pinging {} downstream services...", SERVICE_HEALTH_URLS.size());

        SERVICE_HEALTH_URLS.forEach(url -> {
            webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                    response -> log.info("Keep-alive OK: {}", url),
                    error    -> log.warn("Keep-alive FAILED for {}: {}", url, error.getMessage())
                );
        });
    }
}
