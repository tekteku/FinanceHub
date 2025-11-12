package com.financehub.exception;

/**
 * Exception thrown when a request contains invalid data.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
