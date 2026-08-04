package com.example.networkanomalyassistant.service;

import com.example.networkanomalyassistant.model.DeviceNode;
import com.example.networkanomalyassistant.repo.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TopologyService {

    private final DeviceRepository deviceRepository;

    public TopologyService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public DeviceNode saveDevice(DeviceNode device) {
        return deviceRepository.save(device);
    }

    public List<DeviceNode> getAllDevices() {
        return deviceRepository.findAll();
    }

    public DeviceNode getDeviceById(String id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Device not found with ID: " + id));
    }

    public List<DeviceNode> getBlastRadius(String id) {
        // Ensure device exists first
        getDeviceById(id);
        return deviceRepository.findBlastRadius(id);
    }
}
