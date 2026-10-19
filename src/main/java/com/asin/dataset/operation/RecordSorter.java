package com.asin.dataset.operation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/** Sorts records by a field. Numbers compare numerically, everything else as text. Missing values go last. */
@Component
public class RecordSorter implements RecordOperation<List<Map<String, Object>>> {

    @Override
    public List<Map<String, Object>> apply(List<Map<String, Object>> records, String field) {
        return sort(records, field, SortOrder.ASC);
    }

    public List<Map<String, Object>> sort(List<Map<String, Object>> records, String field, SortOrder order) {
        List<Map<String, Object>> sorted = new ArrayList<>(records);
        sorted.sort(comparator(field, order));
        return sorted;
    }

    private Comparator<Map<String, Object>> comparator(String field, SortOrder order) {
        return (r1, r2) -> {
            Object a = r1.get(field);
            Object b = r2.get(field);
            if (a == null && b == null) return 0;
            if (a == null) return 1;
            if (b == null) return -1;
            int result = compareValues(a, b);
            return order == SortOrder.DESC ? -result : result;
        };
    }

    private int compareValues(Object a, Object b) {
        if (a instanceof Number x && b instanceof Number y) {
            return Double.compare(x.doubleValue(), y.doubleValue());
        }
        return String.valueOf(a).compareToIgnoreCase(String.valueOf(b));
    }
}
