package com.club.badminton.exception;

/**
 * Thrown when a request violates domain-specific club policies,
 * resource caps, or timing conditions.
 */
public class BusinessValidationException extends RuntimeException {

    public BusinessValidationException(String message) {
        super(message);
    }

    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}