package com.bristol.domain.shared.exception;

/**
 * Base exception for all domain-level errors.
 * Domain exceptions represent business rule violations.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
