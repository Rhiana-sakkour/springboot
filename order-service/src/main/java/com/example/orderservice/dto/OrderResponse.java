package com.example.orderservice.dto;

import com.example.orderservice.entity.Order;

/** Order data returned to the client (the entity is never exposed directly). */
public record OrderResponse(
        Long orderId,
        Long userId,
        String userName,
        String productName,
        Integer quantity,
        String status) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getUserName(),
                order.getProductName(),
                order.getQuantity(),
                order.getStatus().name());
    }
}
