package com.asin.dataset.operation;

import static org.junit.jupiter.api.Assertions.*;

import com.asin.dataset.exception.InvalidRequestException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RecordSorterTest {

    private final RecordSorter sorter = new RecordSorter();

    private final List<Map<String, Object>> records = List.of(
            Map.of("id", 1, "age", 30),
            Map.of("id", 2, "age", 25),
            Map.of("id", 3, "age", 28),
            Map.of("id", 4));

    @Test
    void sortsNumbersAscendingWithMissingValuesLast() {
        var result = sorter.sort(records, "age", SortOrder.ASC);
        assertEquals(List.of(2, 3, 1, 4), result.stream().map(r -> r.get("id")).toList());
    }

    @Test
    void sortsDescendingWithMissingValuesStillLast() {
        var result = sorter.sort(records, "age", SortOrder.DESC);
        assertEquals(List.of(1, 3, 2, 4), result.stream().map(r -> r.get("id")).toList());
    }

    @Test
    void numbersAreComparedNumericallyNotAsText() {
        var data = List.<Map<String, Object>>of(Map.of("n", 100), Map.of("n", 9));
        assertEquals(9, sorter.sort(data, "n", SortOrder.ASC).get(0).get("n"));
    }

    @Test
    void sortsStringsCaseInsensitively() {
        var data = List.<Map<String, Object>>of(Map.of("name", "bob"), Map.of("name", "Alice"));
        assertEquals("Alice", sorter.sort(data, "name", SortOrder.ASC).get(0).get("name"));
    }

    @Test
    void invalidOrderIsRejected() {
        assertThrows(InvalidRequestException.class, () -> SortOrder.from("sideways"));
        assertEquals(SortOrder.ASC, SortOrder.from(null));
        assertEquals(SortOrder.DESC, SortOrder.from("DESC"));
    }
}
