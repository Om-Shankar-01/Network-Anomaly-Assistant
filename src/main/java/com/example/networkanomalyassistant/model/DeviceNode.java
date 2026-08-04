package com.example.networkanomalyassistant.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Device")
public class DeviceNode {

    @Id
    private String id;

    @Property("hostname")
    private String hostname;

    @Property("ipAddress")
    private String ipAddress;

    @Property("deviceType")
    private String deviceType; // ROUTER, SWITCH, SERVER, FIREWALL, LOAD_BALANCER

    @Property("status")
    private String status; // HEALTHY, ANOMALOUS, DOWN, UNREACHABLE

    @Relationship(type = "HAS_INTERFACE", direction = Relationship.Direction.OUTGOING)
    private Set<InterfaceNode> interfaces = new HashSet<>();

    @Relationship(type = "DEPENDS_ON", direction = Relationship.Direction.OUTGOING)
    private Set<DependencyEdge> dependencies = new HashSet<>();

    public DeviceNode() {}

    public DeviceNode(String id, String hostname, String ipAddress, String deviceType, String status) {
        this.id = id;
        this.hostname = hostname;
        this.ipAddress = ipAddress;
        this.deviceType = deviceType;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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

    public Set<InterfaceNode> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Set<InterfaceNode> interfaces) {
        this.interfaces = interfaces;
    }

    public Set<DependencyEdge> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<DependencyEdge> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependency(DeviceNode target, String dependencyType, Double weight) {
        this.dependencies.add(new DependencyEdge(target, dependencyType, weight));
    }
}
