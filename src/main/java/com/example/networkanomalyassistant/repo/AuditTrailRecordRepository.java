package com.example.networkanomalyassistant.repo;

import com.example.networkanomalyassistant.entity.AuditTrailRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditTrailRecordRepository extends JpaRepository<AuditTrailRecord, Long> {

    List<AuditTrailRecord> findByIncidentIdOrderByTimestampUtcAsc(String incidentId);

    List<AuditTrailRecord> findByDeviceIdOrderByTimestampUtcDesc(String deviceId);
}