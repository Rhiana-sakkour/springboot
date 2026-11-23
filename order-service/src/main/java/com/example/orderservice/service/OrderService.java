package com.example.orderservice.service;

import com.example.orderservice.client.UserClient;
import com.example.orderservice.client.UserInfo;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Business logic of order creation. */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final UserClient userClient;
    private final OrderRepository orderRepository;

    public OrderService(UserClient userClient, OrderRepository orderRepository) {
        this.userClient = userClient;
        this.orderRepository = orderRepository;
    }

    /**
     * Steps 3-6 of the flow: verify the user first (gRPC), save only afterwards.
     * If the user lookup throws, nothing is saved. This method is deliberately NOT @Transactional,
     * so no database connection is held while waiting for the remote call.
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        UserInfo user = userClient.getUser(request.userId());

        Order order = new Order(
                user.id(),
                user.name(),
                request.productName().trim(),
                request.quantity(),
                OrderStatus.CREATED);
        Order saved = orderRepository.save(order);

        log.info("Order {} created for user {} ({})", saved.getId(), user.id(), user.name());
        return OrderResponse.from(saved);
    }

    /** Helper used to verify what has been stored (e.g. that rejected orders were not saved). */
    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(OrderResponse::from).toList();
    }
}
