package com.company.employees.domain;

import java.util.regex.Pattern;

/**
 * Shared employee validation rules used by both the domain model and request DTOs.
 */
public final class EmployeeValidationRules {
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_EMAIL_LENGTH = 255;
    public static final int MAX_DEPARTMENT_LENGTH = 50;

    public static final String NAME_REQUIRED_MESSAGE = "Employee name cannot be empty";
    public static final String NAME_LENGTH_MESSAGE = "Employee name must be between 2 and 100 characters";
    public static final String EMAIL_REQUIRED_MESSAGE = "Employee email cannot be empty";
    public static final String EMAIL_FORMAT_MESSAGE = "Employee email format is invalid";
    public static final String DEPARTMENT_REQUIRED_MESSAGE = "Employee department cannot be empty";
    public static final String DEPARTMENT_LENGTH_MESSAGE = "Employee department name must not exceed 50 characters";

    public static final Pattern COMPILED_EMAIL_PATTERN = Pattern.compile(EMAIL_PATTERN);

    private EmployeeValidationRules() {
    }
}