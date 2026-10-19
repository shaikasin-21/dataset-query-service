package com.asin.dataset.service;

import com.asin.dataset.dto.InsertResponse;
import com.asin.dataset.dto.QueryResponse;
import com.asin.dataset.exception.DatasetNotFoundException;
import com.asin.dataset.exception.InvalidRequestException;
import com.asin.dataset.model.DatasetRecord;
import com.asin.dataset.operation.RecordGrouper;
import com.asin.dataset.operation.RecordSorter;
import com.asin.dataset.operation.SortOrder;
import com.asin.dataset.repository.DatasetRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatasetService {

    private static final Pattern DATASET_NAME = Pattern.compile("^[A-Za-z0-9_-]{1,100}$");
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final DatasetRecordRepository repository;
    private final ObjectMapper mapper;
    private final RecordGrouper grouper;
    private final RecordSorter sorter;

    public DatasetService(DatasetRecordRepository repository, ObjectMapper mapper,
                          RecordGrouper grouper, RecordSorter sorter) {
        this.repository = repository;
        this.mapper = mapper;
        this.grouper = grouper;
        this.sorter = sorter;
    }

    @Transactional
    public InsertResponse addRecord(String datasetName, JsonNode body) {
        validateDatasetName(datasetName);
        if (body == null || !body.isObject() || body.isEmpty()) {
            throw new InvalidRequestException("Request body must be a non-empty JSON object");
        }
        DatasetRecord saved = repository.save(new DatasetRecord(datasetName, body.toString()));

        JsonNode idNode = body.get("id");
        Object recordId = saved.getId();
        if (idNode != null && !idNode.isNull()) {
            recordId = idNode.isIntegralNumber() ? (Object) idNode.asLong() : idNode.asText();
        }
        return new InsertResponse("Record added successfully", datasetName, recordId);
    }

    @Transactional(readOnly = true)
    public QueryResponse query(String datasetName, String groupBy, String sortBy, String order) {
        validateDatasetName(datasetName);
        groupBy = blankToNull(groupBy);
        sortBy = blankToNull(sortBy);
        order = blankToNull(order);

        if (order != null && sortBy == null) {
            throw new InvalidRequestException("'order' can only be used together with 'sortBy'");
        }
        SortOrder sortOrder = SortOrder.from(order);

        List<DatasetRecord> entities = repository.findByDatasetNameOrderByIdAsc(datasetName);
        if (entities.isEmpty()) {
            throw new DatasetNotFoundException(datasetName);
        }
        List<Map<String, Object>> records = entities.stream().map(this::parse).toList();

        if (groupBy != null) {
            Map<String, List<Map<String, Object>>> groups = grouper.apply(records, groupBy);
            if (sortBy != null) {
                final String field = sortBy;
                groups.replaceAll((key, list) -> sorter.sort(list, field, sortOrder));
            }
            return QueryResponse.grouped(groups);
        }
        if (sortBy != null) {
            return QueryResponse.sorted(sorter.sort(records, sortBy, sortOrder));
        }
        return QueryResponse.all(records);
    }

    private Map<String, Object> parse(DatasetRecord entity) {
        try {
            return mapper.readValue(entity.getJsonData(), MAP_TYPE);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Stored record " + entity.getId() + " is corrupted", e);
        }
    }

    private void validateDatasetName(String name) {
        if (name == null || !DATASET_NAME.matcher(name).matches()) {
            throw new InvalidRequestException(
                    "Dataset name must be 1-100 characters: letters, digits, '_' or '-'");
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
