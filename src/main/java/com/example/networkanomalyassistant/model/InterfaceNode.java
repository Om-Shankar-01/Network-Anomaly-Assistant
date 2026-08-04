package com.example.networkanomalyassistant.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Interface")
public class InterfaceNode {

    @Id
    private String id;

    @Property("name")
    private String name;

    @Property("macAddress")
    private String macAddress;

    @Property("bandwidthMbps")
    private Long bandwidthMbps;

    @Property("status")
    private String status; // UP, DOWN, TESTING

    public InterfaceNode() {}

    public InterfaceNode(String id, String name, String macAddress, Long bandwidthMbps, String status) {
        this.id = id;
        this.name = name;
        this.macAddress = macAddress;
        this.bandwidthMbps = bandwidthMbps;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Long getBandwidthMbps() {
        return bandwidthMbps;
    }

    public void setBandwidthMbps(Long bandwidthMbps) {
        this.bandwidthMbps = bandwidthMbps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
