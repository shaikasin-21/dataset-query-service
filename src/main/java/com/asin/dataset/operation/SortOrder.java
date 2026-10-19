package com.asin.dataset.operation;

import com.asin.dataset.exception.InvalidRequestException;

public enum SortOrder {
    ASC, DESC;

    /** Parses "asc"/"desc" (case-insensitive). Null or blank defaults to ASC. */
    public static SortOrder from(String value) {
        if (value == null || value.isBlank()) {
            return ASC;
        }
        return switch (value.trim().toLowerCase()) {
            case "asc" -> ASC;
            case "desc" -> DESC;
            default -> throw new InvalidRequestException("Invalid order '" + value + "'. Use 'asc' or 'desc'");
        };
    }
}
