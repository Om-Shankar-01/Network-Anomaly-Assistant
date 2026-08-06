package com.example.networkanomalyassistant.service;

import com.example.networkanomalyassistant.dto.DagGraph;
import com.example.networkanomalyassistant.dto.DagNode;
import com.example.networkanomalyassistant.model.DeviceNode;
import com.example.networkanomalyassistant.repo.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CausalGraphBuilderService {

    private final DeviceRepository deviceRepository;

    public CausalGraphBuilderService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * Builds a localized Directed Acyclic Graph (DAG) centered around an incident device.
     */
    public DagGraph buildCausalDag(String deviceId) {
        DagGraph dag = new DagGraph(deviceId);

        // Fetch target device from Neo4j
        DeviceNode targetDevice = deviceRepository.findById(deviceId).orElse(null);
        if (targetDevice != null) {
            DagNode targetDagNode = new DagNode(
                    targetDevice.getId(),
                    targetDevice.getHostname(),
                    targetDevice.getDeviceType(),
                    targetDevice.getStatus()
            );
            dag.addNode(targetDagNode);
        }

        // Fetch downstream blast radius (dependent devices)
        List<DeviceNode> blastRadius = deviceRepository.findBlastRadius(deviceId);
        for (DeviceNode dependent : blastRadius) {
            DagNode childDagNode = new DagNode(dependent.getId(), dependent.getHostname(), dependent.getDeviceType(), dependent.getStatus());
            dag.addNode(childDagNode);

            // Add directed edge: Target Device (Parent) -> Dependent Device (Child)
            dag.addDirectedEdge(deviceId, dependent.getId());
        }

        return dag;
    }
}
