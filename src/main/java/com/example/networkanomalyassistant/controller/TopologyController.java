package com.example.networkanomalyassistant.controller;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.model.DeviceNode;
import com.example.networkanomalyassistant.service.TopologyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topology")
@Tag(name = "Topology Graph", description = "APIs for managing network devices and dependency topology")
public class TopologyController {

    private final TopologyService topologyService;

    public TopologyController(TopologyService topologyService) {
        this.topologyService = topologyService;
    }

    @PostMapping("/devices")
    @Operation(summary = "Register or update a network device node")
    public ApiResponse<DeviceNode> saveDevice(@RequestBody DeviceNode device) {
        DeviceNode saved = topologyService.saveDevice(device);
        return ApiResponse.ok(saved, "Device saved successfully");
    }

    @GetMapping("/devices")
    @Operation(summary = "Get all registered network devices in topology")
    public ApiResponse<List<DeviceNode>> getAllDevices() {
        List<DeviceNode> devices = topologyService.getAllDevices();
        return ApiResponse.ok(devices);
    }

    @GetMapping("/devices/{id}")
    @Operation(summary = "Get a specific network device by ID")
    public ApiResponse<DeviceNode> getDeviceById(@PathVariable String id) {
        DeviceNode device = topologyService.getDeviceById(id);
        return ApiResponse.ok(device);
    }

    @GetMapping("/devices/{id}/blast-radius")
    @Operation(summary = "Calculate downstream blast radius for a given device failure")
    public ApiResponse<List<DeviceNode>> getBlastRadius(@PathVariable String id) {
        List<DeviceNode> impacted = topologyService.getBlastRadius(id);
        return ApiResponse.ok(impacted, "Blast radius calculated successfully");
    }
}
