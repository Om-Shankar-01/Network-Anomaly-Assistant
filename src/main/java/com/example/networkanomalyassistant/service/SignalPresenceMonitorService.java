package com.example.networkanomalyassistant.service;

import com.example.networkanomalyassistant.dto.MissingEvidenceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SignalPresenceMonitorService {

    private static final Logger log = LoggerFactory.getLogger(SignalPresenceMonitorService.class);

    // Expected heartbeat threshold (30 seconds without a signal triggers MISSING_EVIDENCE)
    private static final long HEARTBEAT_TIMEOUT_SECONDS = 30;

    // Registry tracking last seen timestamp per device: deviceId -> Instant
    private final Map<String, Instant> deviceLastSeenRegistry = new ConcurrentHashMap<>();

    /**
     * Called whenever a device emits a telemetry, log, or alert signal
     */
    public void recordDeviceHeartbeat(String deviceId) {
        if (deviceId != null && !deviceId.isBlank()) {
            deviceLastSeenRegistry.put(deviceId, Instant.now());
        }
    }

    /**
     * Scheduled monitor running every 10 seconds to audit signal gaps
     */
    @Scheduled(fixedRate = 10000)
    public List<MissingEvidenceEvent> auditSignalGaps() {
        Instant now = Instant.now();
        List<MissingEvidenceEvent> missingEvents = new ArrayList<>();

        for (Map.Entry<String, Instant> entry : deviceLastSeenRegistry.entrySet()) {
            String deviceId = entry.getKey();
            Instant lastSeen = entry.getValue();
            long secondsOverdue = Duration.between(lastSeen, now).getSeconds();

            if (secondsOverdue > HEARTBEAT_TIMEOUT_SECONDS) {
                MissingEvidenceEvent missingEvent = new MissingEvidenceEvent(
                        deviceId,
                        "GLOBAL",
                        "HEARTBEAT_GAP_TIMEOUT",
                        lastSeen,
                        secondsOverdue,
                        secondsOverdue > 60 ? "CRITICAL" : "WARNING"
                );

                missingEvents.add(missingEvent);
                log.warn("MISSING_EVIDENCE DETECTED! Device [{}] silent for {} seconds (Category: {})",
                        deviceId, secondsOverdue, missingEvent.getEvidenceCategory());
            }
        }

        return missingEvents;
    }

    public Map<String, Instant> getDeviceRegistry() {
        return new HashMap<>(deviceLastSeenRegistry);
    }
}