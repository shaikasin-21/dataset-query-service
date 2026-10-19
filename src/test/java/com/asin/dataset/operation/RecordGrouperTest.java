package com.asin.dataset.operation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RecordGrouperTest {

    private final RecordGrouper grouper = new RecordGrouper();

    @Test
    void groupsRecordsByFieldValue() {
        var records = List.<Map<String, Object>>of(
                Map.of("id", 1, "department", "Engineering"),
                Map.of("id", 2, "department", "Engineering"),
                Map.of("id", 3, "department", "Marketing"));

        var groups = grouper.apply(records, "department");

        assertEquals(2, groups.size());
        assertEquals(2, groups.get("Engineering").size());
        assertEquals(1, groups.get("Marketing").size());
    }

    @Test
    void recordsMissingTheFieldGoToUngrouped() {
        var records = List.<Map<String, Object>>of(Map.of("id", 1), Map.of("id", 2, "department", "HR"));

        var groups = grouper.apply(records, "department");

        assertEquals(1, groups.get(RecordGrouper.UNGROUPED).size());
        assertEquals(1, groups.get("HR").size());
    }
}
