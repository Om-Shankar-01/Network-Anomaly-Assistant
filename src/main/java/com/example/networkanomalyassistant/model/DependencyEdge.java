package com.example.networkanomalyassistant.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class DependencyEdge {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private DeviceNode targetDevice;

    @Property("dependencyType")
    private String dependencyType; // PHYSICAL, BGP, OSPF, SERVICE, LOGICAL

    @Property("weight")
    private Double weight;

    public DependencyEdge() {}

    public DependencyEdge(DeviceNode targetDevice, String dependencyType, Double weight) {
        this.targetDevice = targetDevice;
        this.dependencyType = dependencyType;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public DeviceNode getTargetDevice() {
        return targetDevice;
    }

    public void setTargetDevice(DeviceNode targetDevice) {
        this.targetDevice = targetDevice;
    }

    public String getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(String dependencyType) {
        this.dependencyType = dependencyType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
