package com.company.employees.domain;

/**
 * Thrown when an Employee violates domain rules.
 */
public class InvalidEmployeeException extends DomainException {
    public InvalidEmployeeException(String message) {
        super(message);
    }
}
