package com.example.networkanomalyassistant.service;

import com.example.networkanomalyassistant.dto.IncidentContextDto;
import com.example.networkanomalyassistant.entity.IncidentRecord;
import com.example.networkanomalyassistant.repo.IncidentRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Service
public class ExplainableAssistantService {

    private static final Logger log = LoggerFactory.getLogger(ExplainableAssistantService.class);
    private static final String PYTHON_LLM_REPORT_URL = "http://localhost:8000/generate/incident-report";

    private final IncidentContextAggregatorService aggregatorService;
    private final IncidentRecordRepository incidentRecordRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public ExplainableAssistantService(IncidentContextAggregatorService aggregatorService,
                                       IncidentRecordRepository incidentRecordRepository) {
        this.aggregatorService = aggregatorService;
        this.incidentRecordRepository = incidentRecordRepository;
    }

    /**
     * Aggregates context, calls Python LLM Engine to generate Markdown report, and persists to TimescaleDB.
     */
    public Map<String, Object> generateAndPersistReport(String deviceId) {
        // 1. Aggregate multi-source context (3-way evidence matrix)
        IncidentContextDto context = aggregatorService.aggregateContext(deviceId);

        // 2. Call Python LLM Service to generate Markdown report + CLI commands
        @SuppressWarnings("unchecked")
        Map<String, Object> reportResponse = restTemplate.postForObject(PYTHON_LLM_REPORT_URL, context, Map.class);

        if (reportResponse != null && reportResponse.containsKey("markdown_report")) {
            String markdown = reportResponse.get("markdown_report").toString();

            // 3. Persist incident record in TimescaleDB
            IncidentRecord incident = new IncidentRecord(
                    deviceId,
                    context.getConfidenceRating(),
                    markdown
            );
            incident.setEndTime(Instant.now());
            incidentRecordRepository.save(incident);

            log.info("Persisted Explainable Incident Report [{}] for device [{}] into TimescaleDB",
                    incident.getIncidentId(), deviceId);
        }

        return reportResponse;
    }
}