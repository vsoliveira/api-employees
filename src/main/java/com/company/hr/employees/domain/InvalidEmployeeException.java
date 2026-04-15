package com.company.hr.employees.domain;

import com.company.hr.shared.domain.DomainException;

/**
 * Thrown when an Employee violates domain rules.
 */
public class InvalidEmployeeException extends DomainException {
    private static final long serialVersionUID = 1L;

    public InvalidEmployeeException(String message) {
        super(message);
    }
}
