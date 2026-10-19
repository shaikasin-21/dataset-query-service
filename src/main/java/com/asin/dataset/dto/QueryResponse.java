package com.asin.dataset.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

/** Only the section relevant to the requested operation is serialized. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record QueryResponse(
        Map<String, List<Map<String, Object>>> groupedRecords,
        List<Map<String, Object>> sortedRecords,
        List<Map<String, Object>> records) {

    public static QueryResponse grouped(Map<String, List<Map<String, Object>>> groups) {
        return new QueryResponse(groups, null, null);
    }

    public static QueryResponse sorted(List<Map<String, Object>> sorted) {
        return new QueryResponse(null, sorted, null);
    }

    public static QueryResponse all(List<Map<String, Object>> all) {
        return new QueryResponse(null, null, all);
    }
}
