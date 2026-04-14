package com.company.employees.domain;

/**
 * Thrown when an Employee violates domain rules.
 */
public class InvalidEmployeeException extends DomainException {
    private static final long serialVersionUID = 1L;

    public InvalidEmployeeException(String message) {
        super(message);
    }
}
