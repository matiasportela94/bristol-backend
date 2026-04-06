package com.bristol.domain.shared.exception;

/**
 * Thrown when domain validation fails.
 */
public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message);
    }
}
