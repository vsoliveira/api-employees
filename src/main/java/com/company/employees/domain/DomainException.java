package com.company.employees.domain;

/**
 * Base exception for all domain exceptions.
 */
public abstract class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
