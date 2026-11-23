package com.example.orderservice.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    /**
     * One long-lived channel to user-service (channels are thread-safe and meant to be shared).
     * The connection is opened lazily, so order-service starts even when user-service is down.
     */
    @Bean(destroyMethod = "shutdown")
    public ManagedChannel userServiceChannel(@Value("${user-service.grpc.host}") String host,
                                             @Value("${user-service.grpc.port}") int port) {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }
}
