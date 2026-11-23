package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Body of POST /api/orders. */
public record CreateOrderRequest(
        @NotNull(message = "userId is required")
        @Positive(message = "userId must be a positive number")
        Long userId,

        @NotBlank(message = "productName is required")
        String productName,

        @NotNull(message = "quantity is required")
        @Positive(message = "quantity must be at least 1")
        Integer quantity) {
}
