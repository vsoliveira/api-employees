package com.company.hr.employees.domain;

import com.company.hr.shared.domain.DomainException;

/**
 * Thrown when an Employee cannot be found.
 */
public class EmployeeNotFoundException extends DomainException {
    private static final long serialVersionUID = 1L;

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
