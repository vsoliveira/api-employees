package com.company.employees.domain;

import java.util.Arrays;
import java.util.Objects;

/**
 * Supported sort directions for listing queries.
 */
public enum SortDirection {
    ASC("asc"),
    DESC("desc");

    private final String parameterValue;

    SortDirection(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public static SortDirection from(String value) {
        Objects.requireNonNull(value, "Sort direction must not be null");
        return Arrays.stream(values())
                .filter(direction -> direction.parameterValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported sort direction: " + value + ". Supported values are: asc, desc"
                ));
    }
}