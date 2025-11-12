package com.financehub.exception;

/**
 * Exception thrown when authentication fails or credentials are invalid.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
