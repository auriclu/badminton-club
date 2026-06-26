package com.club.badminton.exception;

/**
 * Thrown when an unrecoverable database or persistence tier error occurs,
 * such as connectivity losses, pool exhaustion, or faulty statements.
 */
public class DatabaseAccessException extends RuntimeException {

    public DatabaseAccessException(String message) {
        super(message);
    }

    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}