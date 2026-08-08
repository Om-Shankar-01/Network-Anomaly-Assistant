package com.example.networkanomalyassistant.dto;

import java.time.Instant;
import java.util.*;

public class IncidentContextDto {

    private String incidentId;
    private String primaryDeviceId;
    private String rootCauseHypothesis;
    private double confidenceScore;
    private String confidenceRating;
    private Instant timestampUtc;

    // The 3-Way Evidence Matrix
    private List<String> confirmedEvidence = new ArrayList<>();
    private List<String> correlatedSignals = new ArrayList<>();
    private List<String> missingEvidence = new ArrayList<>();
    private List<String> recentLogs = new ArrayList<>();
    private Map<String, Object> metricsSummary = new HashMap<>();


    public IncidentContextDto() {
        this.timestampUtc = Instant.now();
    }

    public IncidentContextDto(String incidentId, String primaryDeviceId, String rootCauseHypothesis, double confidenceScore, String confidenceRating) {
        this.incidentId = incidentId;
        this.primaryDeviceId = primaryDeviceId;
        this.rootCauseHypothesis = rootCauseHypothesis;
        this.confidenceScore = confidenceScore;
        this.confidenceRating = confidenceRating;
        this.timestampUtc = Instant.now();
    }


    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getPrimaryDeviceId() {
        return primaryDeviceId;
    }

    public void setPrimaryDeviceId(String primaryDeviceId) {
        this.primaryDeviceId = primaryDeviceId;
    }

    public String getRootCauseHypothesis() {
        return rootCauseHypothesis;
    }

    public void setRootCauseHypothesis(String rootCauseHypothesis) {
        this.rootCauseHypothesis = rootCauseHypothesis;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getConfidenceRating() {
        return confidenceRating;
    }

    public void setConfidenceRating(String confidenceRating) {
        this.confidenceRating = confidenceRating;
    }

    public Instant getTimestampUtc() {
        return timestampUtc;
    }

    public void setTimestampUtc(Instant timestampUtc) {
        this.timestampUtc = timestampUtc;
    }

    public List<String> getConfirmedEvidence() {
        return confirmedEvidence;
    }

    public void setConfirmedEvidence(List<String> confirmedEvidence) {
        this.confirmedEvidence = confirmedEvidence;
    }

    public List<String> getCorrelatedSignals() {
        return correlatedSignals;
    }

    public void setCorrelatedSignals(List<String> correlatedSignals) {
        this.correlatedSignals = correlatedSignals;
    }

    public List<String> getMissingEvidence() {
        return missingEvidence;
    }

    public void setMissingEvidence(List<String> missingEvidence) {
        this.missingEvidence = missingEvidence;
    }

    public List<String> getRecentLogs() {
        return recentLogs;
    }

    public void setRecentLogs(List<String> recentLogs) {
        this.recentLogs = recentLogs;
    }

    public Map<String, Object> getMetricsSummary() {
        return metricsSummary;
    }

    public void setMetricsSummary(Map<String, Object> metricsSummary) {
        this.metricsSummary = metricsSummary;
    }
}
