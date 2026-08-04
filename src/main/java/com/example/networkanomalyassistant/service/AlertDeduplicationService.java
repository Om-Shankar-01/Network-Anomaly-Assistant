package com.example.networkanomalyassistant.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.networkanomalyassistant.dto.AlertEvent;

@Service
public class AlertDeduplicationService {
    
    private static final Logger log = LoggerFactory.getLogger(AlertDeduplicationService.class);
    private static final long WINDOW_DURATION_SECONDS = 30;

    // In-memory sliding window cache: FingerprintKey -> AlertEvent
    private final Map<String, AlertEvent> slidingWindowCache = new ConcurrentHashMap<>();

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.alerts}")
    private String alertsTopic;
    public AlertDeduplicationService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Process incoming raw alert and perform deduplication
     */
    public synchronized AlertEvent processAlert(AlertEvent incomingAlert) {
        String fingerprint = incomingAlert.getFingerprintKey();
        Instant now = Instant.now();

        if (slidingWindowCache.containsKey(fingerprint)) {
            AlertEvent existingAlert = slidingWindowCache.get(fingerprint);
            
            // Check if existing alert is still within 30-second sliding window
            if (Duration.between(existingAlert.getFirstSeen(), now).getSeconds() <= WINDOW_DURATION_SECONDS) {
                existingAlert.incrementOccurrence();
                log.info("Deduplicated alert [{}]. Total count: {}", fingerprint, existingAlert.getOccurrenceCount());
                return existingAlert;
            }
        }

        // New alert or window expired: place into cache
        incomingAlert.setFirstSeen(now);
        incomingAlert.setLastSeen(now);
        slidingWindowCache.put(fingerprint, incomingAlert);

        // Publish deduplicated root alert event to Kafka
        kafkaTemplate.send(alertsTopic, incomingAlert.getSourceDeviceId(), incomingAlert);
        log.info("New alert event published to Kafka [{}]: {}", alertsTopic, fingerprint);

        return incomingAlert;
    }

    /**
     * Scheduled cleanup job running every 10 seconds to flush expired window alerts
     */
    @Scheduled(fixedRate = 10000)
    public void flushExpiredWindowCache() {
        Instant now = Instant.now();
        slidingWindowCache.entrySet().removeIf(entry -> {
            boolean expired = Duration.between(entry.getValue().getLastSeen(), now).getSeconds() > WINDOW_DURATION_SECONDS;
            if (expired) {
                log.debug("Flushed expired deduplication cache window for key: {}", entry.getKey());
            }
            return expired;
        });
    }

    public List<AlertEvent> getActiveWindowAlerts() {
        return new ArrayList<>(slidingWindowCache.values());
    }
}
