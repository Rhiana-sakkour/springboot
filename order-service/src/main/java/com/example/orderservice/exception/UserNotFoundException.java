package com.example.orderservice.exception;

/** The user does not exist in user-service (gRPC status NOT_FOUND). */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(long userId) {
        super("User not found: " + userId);
    }
}
