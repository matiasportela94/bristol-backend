package com.bristol.application.common;

/**
 * Marker interface for use cases.
 * Use cases represent application-level business operations.
 */
public interface UseCase<INPUT, OUTPUT> {

    OUTPUT execute(INPUT input);
}
