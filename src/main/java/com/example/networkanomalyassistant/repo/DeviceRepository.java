package com.example.networkanomalyassistant.repo;

import com.example.networkanomalyassistant.model.DeviceNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends Neo4jRepository<DeviceNode, String> {

    Optional<DeviceNode> findByHostname(String hostname);

    List<DeviceNode> findByDeviceType(String deviceType);

    List<DeviceNode> findByStatus(String status);

    @Query("MATCH (d:Device {id: $deviceId})-[r:DEPENDS_ON*1..5]->(impacted:Device) RETURN DISTINCT impacted")
    List<DeviceNode> findBlastRadius(String deviceId);
}
