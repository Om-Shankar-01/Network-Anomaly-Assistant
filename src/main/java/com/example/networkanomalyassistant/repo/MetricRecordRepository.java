package com.example.networkanomalyassistant.repo;


import com.example.networkanomalyassistant.entity.MetricRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MetricRecordRepository extends JpaRepository<MetricRecord, Long> {

    List<MetricRecord> findBySourceDeviceIdAndTimestampUtcBetweenOrderByTimestampUtcAsc(
            String sourceDeviceId, Instant start, Instant end
    );

    List<MetricRecord> findBySourceDeviceIdAndMetricNameOrderByTimestampUtcDesc(
            String sourceDeviceId, String metricName
    );
}
