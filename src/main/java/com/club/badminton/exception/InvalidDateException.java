package com.club.badminton.exception;

/**
 * Thrown when an unrecoverable user enters invalid date
 */
public class InvalidDateException extends RuntimeException {

    public InvalidDateException(String message) {
        super(message);
    }

    public InvalidDateException(String message, Throwable cause) {
        super(message, cause);
    }
}