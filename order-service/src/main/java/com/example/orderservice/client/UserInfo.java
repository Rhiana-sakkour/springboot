package com.example.orderservice.client;

/** User data received from user-service (independent of the generated gRPC classes). */
public record UserInfo(long id, String name, String email) {
}
