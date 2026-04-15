package com.company.hr.shared.domain;

/**
 * Base exception for all domain exceptions.
 */
public abstract class DomainException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DomainException(String message) {
        super(message);
    }
}
