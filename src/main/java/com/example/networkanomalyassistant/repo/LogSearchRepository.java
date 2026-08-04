package com.example.networkanomalyassistant.repo;

import com.example.networkanomalyassistant.document.LogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogSearchRepository extends ElasticsearchRepository<LogDocument, String> {

    List<LogDocument> findByLogMessageContainingIgnoreCase(String text);

    List<LogDocument> findBySourceDeviceIdOrderByTimestampDesc(String sourceDeviceId);
}
