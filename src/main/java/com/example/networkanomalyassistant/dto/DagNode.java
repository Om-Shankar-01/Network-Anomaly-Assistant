package com.example.networkanomalyassistant.dto;

import java.util.HashSet;
import java.util.Set;

public class DagNode {

    private String id;
    private String hostName;
    private String deviceType;
    private String status;
    private double anomalyScore;
    private Set<String> parentIds = new HashSet<>();   // Upstream ancestors
    private Set<String> childrenIds = new HashSet<>(); // Downstream dependents

    public DagNode() {}

    public DagNode(String id, String hostName, String deviceType, String status) {
        this.id = id;
        this.hostName = hostName;
        this.deviceType = deviceType;
        this.status = status;
        this.anomalyScore = 0.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAnomalyScore() {
        return anomalyScore;
    }

    public void setAnomalyScore(double anomalyScore) {
        this.anomalyScore = anomalyScore;
    }

    public Set<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(Set<String> parentIds) {
        this.parentIds = parentIds;
    }

    public Set<String> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(Set<String> childrenIds) {
        this.childrenIds = childrenIds;
    }
}
