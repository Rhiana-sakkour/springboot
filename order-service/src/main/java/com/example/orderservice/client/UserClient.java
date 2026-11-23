package com.example.orderservice.client;

import com.example.orderservice.exception.UserNotFoundException;
import com.example.orderservice.exception.UserServiceUnavailableException;

/**
 * Port towards user-service. The service layer depends on this interface only,
 * so it knows nothing about gRPC (clean architecture).
 */
public interface UserClient {

    /**
     * @throws UserNotFoundException           if the user does not exist
     * @throws UserServiceUnavailableException if user-service cannot be reached or fails
     */
    UserInfo getUser(long userId);
}
