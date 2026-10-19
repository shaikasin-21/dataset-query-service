package com.asin.dataset.operation;

import java.util.List;
import java.util.Map;

/** Strategy-style contract so new operations (filter, limit, ...) can be added without touching the service. */
public interface RecordOperation<R> {
    R apply(List<Map<String, Object>> records, String field);
}
