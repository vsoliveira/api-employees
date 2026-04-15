package com.company.hr.employees.domain;

import java.util.Arrays;
import java.util.Objects;

/**
 * Supported employee sort fields for listing queries.
 */
public enum EmployeeSortField {
    ID("id"),
    NAME("name"),
    EMAIL("email"),
    DEPARTMENT("department");

    private final String parameterValue;

    EmployeeSortField(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public static EmployeeSortField from(String value) {
        Objects.requireNonNull(value, "Sort field must not be null");
        return Arrays.stream(values())
                .filter(field -> field.parameterValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported sort field: " + value + ". Supported values are: id, name, email, department"
                ));
    }
}