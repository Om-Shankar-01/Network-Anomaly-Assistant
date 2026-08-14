package com.example.networkanomalyassistant.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.networkanomalyassistant.dto.DagGraph;
import com.example.networkanomalyassistant.model.DeviceNode;
import com.example.networkanomalyassistant.repo.DeviceRepository;

@ExtendWith(MockitoExtension.class)
class CausalGraphBuilderServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private CausalGraphBuilderService causalGraphBuilderService;

    @Test
    void testBuildCausalDag_WithTargetAndBlastRadius() {
        String targetDeviceId = "router-core-01";

        DeviceNode targetNode = new DeviceNode();
        targetNode.setId(targetDeviceId);
        targetNode.setHostname("Core Router 01");
        targetNode.setDeviceType("ROUTER");
        targetNode.setStatus("CRITICAL");

        String childDeviceId = "switch-edge-04";
        DeviceNode dependentNode = new DeviceNode();
        dependentNode.setId(childDeviceId);
        dependentNode.setHostname("Edge Switch 04");
        dependentNode.setDeviceType("SWITCH");
        dependentNode.setStatus("HEALTHY");

        when(deviceRepository.findById(targetDeviceId)).thenReturn(Optional.of(targetNode));
        when(deviceRepository.findBlastRadius(targetDeviceId)).thenReturn(List.of(dependentNode));

        DagGraph result = causalGraphBuilderService.buildCausalDag(targetDeviceId);

        // Verify Graph Target
        assertNotNull(result);
        assertEquals(targetDeviceId, result.getTargetDeviceId());

        // Verify nodes exist in the DAG map
        assertEquals(2, result.getNodes().size(), "Graph should contain target + dependent node");
        assertTrue(result.getNodes().containsKey(targetDeviceId));
        assertTrue(result.getNodes().containsKey(childDeviceId));

        // Verify correct directed edge orientation (Root -> Dependent)
        assertTrue(result.getNodes().get(targetDeviceId).getChildrenIds().contains(childDeviceId),
                "Target node should have child switch in its childrenIds");
        assertTrue(result.getNodes().get(childDeviceId).getParentIds().contains(targetDeviceId),
                "Child switch should have target node in its parentIds");
    }
}