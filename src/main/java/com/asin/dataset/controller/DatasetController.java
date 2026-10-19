package com.asin.dataset.controller;

import com.asin.dataset.dto.InsertResponse;
import com.asin.dataset.dto.QueryResponse;
import com.asin.dataset.service.DatasetService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dataset/{datasetName}")
@Tag(name = "Dataset")
public class DatasetController {

    private final DatasetService service;

    public DatasetController(DatasetService service) {
        this.service = service;
    }

    @Operation(summary = "Insert a JSON record into a dataset")
    @PostMapping("/record")
    public ResponseEntity<InsertResponse> addRecord(@PathVariable String datasetName,
                                                    @RequestBody JsonNode body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addRecord(datasetName, body));
    }

    @Operation(summary = "Query a dataset with optional groupBy, sortBy and order (asc|desc)")
    @GetMapping("/query")
    public QueryResponse query(@PathVariable String datasetName,
                               @RequestParam(required = false) String groupBy,
                               @RequestParam(required = false) String sortBy,
                               @RequestParam(required = false) String order) {
        return service.query(datasetName, groupBy, sortBy, order);
    }
}
