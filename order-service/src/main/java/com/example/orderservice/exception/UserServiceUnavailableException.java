package com.example.orderservice.exception;

/** user-service could not be reached or failed (gRPC UNAVAILABLE, DEADLINE_EXCEEDED, ...). */
public class UserServiceUnavailableException extends RuntimeException {

    public UserServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
