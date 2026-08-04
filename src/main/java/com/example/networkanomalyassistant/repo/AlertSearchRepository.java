package com.example.networkanomalyassistant.repo;

import com.example.networkanomalyassistant.document.AlertDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertSearchRepository extends ElasticsearchRepository<AlertDocument, String> {

    List<AlertDocument> findBySourceDeviceId(String sourceDeviceId);

    List<AlertDocument> findBySeverity(String severity);
}
