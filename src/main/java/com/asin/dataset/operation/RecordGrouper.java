package com.asin.dataset.operation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Groups records by the value of a field. Records missing the field go under "_ungrouped". */
@Component
public class RecordGrouper implements RecordOperation<Map<String, List<Map<String, Object>>>> {

    public static final String UNGROUPED = "_ungrouped";

    @Override
    public Map<String, List<Map<String, Object>>> apply(List<Map<String, Object>> records, String field) {
        Map<String, List<Map<String, Object>>> groups = new LinkedHashMap<>();
        for (Map<String, Object> record : records) {
            Object value = record.get(field);
            String key = value == null ? UNGROUPED : String.valueOf(value);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        }
        return groups;
    }
}
