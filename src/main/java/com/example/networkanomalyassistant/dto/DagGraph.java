package com.example.networkanomalyassistant.dto;

import java.util.HashMap;
import java.util.Map;

public class DagGraph {

    private String targetDeviceId;
    private Map<String, DagNode> nodes = new HashMap<>();

    public DagGraph() {}

    public DagGraph(String targetDeviceId) {
        this.targetDeviceId = targetDeviceId;
    }

    public void addNode(DagNode node) {
        nodes.put(node.getId(), node);
    }

    public void addDirectedEdge(String parentId, String childId) {
        if (nodes.containsKey(parentId) && nodes.containsKey(childId)) {
            nodes.get(childId).getParentIds().add(parentId);
            nodes.get(parentId).getChildrenIds().add(childId);
        }
    }

    public String getTargetDeviceId() {
        return targetDeviceId;
    }

    public void setTargetDeviceId(String targetDeviceId) {
        this.targetDeviceId = targetDeviceId;
    }

    public Map<String, DagNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, DagNode> nodes) {
        this.nodes = nodes;
    }
}
