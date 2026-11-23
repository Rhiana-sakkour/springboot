package com.example.userservice.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Starts the gRPC server together with the Spring context and stops it on shutdown.
 * A non-daemon thread waits for termination so the JVM stays alive (this app has no web server).
 */
@Component
public class GrpcServerLifecycle implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(GrpcServerLifecycle.class);

    private final Server server;
    private final int port;
    private volatile boolean running;

    public GrpcServerLifecycle(UserGrpcService userGrpcService,
                               @Value("${grpc.server.port:9090}") int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
                .addService(userGrpcService)
                .build();
    }

    @Override
    public void start() {
        try {
            server.start();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot start the gRPC server on port " + port, e);
        }
        Thread awaitThread = new Thread(() -> {
            try {
                server.awaitTermination();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "grpc-await");
        awaitThread.setDaemon(false);
        awaitThread.start();
        running = true;
        log.info("gRPC server started on port {}", port);
    }

    @Override
    public void stop() {
        server.shutdown();
        try {
            if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
                server.shutdownNow();
            }
        } catch (InterruptedException e) {
            server.shutdownNow();
            Thread.currentThread().interrupt();
        }
        running = false;
        log.info("gRPC server stopped");
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
