package com.company.employees.domain;

/**
 * Thrown when an Employee cannot be found.
 */
public class EmployeeNotFoundException extends DomainException {
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
