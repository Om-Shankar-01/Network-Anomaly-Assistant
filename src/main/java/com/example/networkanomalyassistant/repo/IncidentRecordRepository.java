package com.example.networkanomalyassistant.repo;


import com.example.networkanomalyassistant.entity.IncidentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRecordRepository extends JpaRepository<IncidentRecord, String> {

    List<IncidentRecord> findByStatus(String status);

    List<IncidentRecord> findByPrimaryDeviceIdOrderByStartTimeDesc(String primaryDeviceId);

}