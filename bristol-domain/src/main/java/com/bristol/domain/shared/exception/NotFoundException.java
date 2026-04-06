package com.bristol.domain.shared.exception;

/**
 * Thrown when a requested entity is not found.
 */
public class NotFoundException extends DomainException {

    public NotFoundException(String entityType, String identifier) {
        super(String.format("%s with identifier '%s' not found", entityType, identifier));
    }
}
