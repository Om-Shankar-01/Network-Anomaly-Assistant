package com.example.networkanomalyassistant.controller;


import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.document.LogDocument;
import com.example.networkanomalyassistant.repo.LogSearchRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search/test")
@Tag(name = "Elasticsearch Search Test", description = "Endpoints for indexing and full-text searching logs in Elasticsearch")
public class LogSearchTestController {

    private final LogSearchRepository logSearchRepository;

    public LogSearchTestController(LogSearchRepository logSearchRepository) {
        this.logSearchRepository = logSearchRepository;
    }

    @PostMapping("/create-log")
    @Operation(summary = "Index a sample network log document into Elasticsearch")
    public ApiResponse<LogDocument> createSampleLog(@RequestParam(defaultValue = "BGP neighbor 10.0.0.2 Down - Interface GigE0/1 flap") String message) {
        LogDocument logDoc = new LogDocument(
                "router-core-01",
                "CRITICAL",
                message
        );
        LogDocument saved = logSearchRepository.save(logDoc);
        return ApiResponse.ok(saved, "Log document indexed in Elasticsearch successfully!");
    }

    @GetMapping("/logs")
    @Operation(summary = "Perform full-text search across all indexed network logs")
    public ApiResponse<List<LogDocument>> searchLogs(@RequestParam(defaultValue = "BGP") String query) {
        List<LogDocument> results = logSearchRepository.findByLogMessageContainingIgnoreCase(query);
        return ApiResponse.ok(results, "Found " + results.size() + " matching log documents");
    }
}
