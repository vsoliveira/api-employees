package com.company.employees.domain;

/**
 * Thrown when an Employee with a given email already exists.
 */
public class EmployeeAlreadyExistsException extends DomainException {
    private static final long serialVersionUID = 1L;

    public EmployeeAlreadyExistsException(String email) {
        super("Employee with email '" + email + "' already exists");
    }
}
