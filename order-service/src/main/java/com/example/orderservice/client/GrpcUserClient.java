package com.example.orderservice.client;

import com.example.grpc.user.GetUserRequest;
import com.example.grpc.user.GetUserResponse;
import com.example.grpc.user.UserServiceGrpc;
import com.example.orderservice.exception.UserNotFoundException;
import com.example.orderservice.exception.UserServiceUnavailableException;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/** gRPC adapter: calls user-service and translates gRPC statuses into domain exceptions. */
@Component
public class GrpcUserClient implements UserClient {

    private static final Logger log = LoggerFactory.getLogger(GrpcUserClient.class);

    private final UserServiceGrpc.UserServiceBlockingStub stub;
    private final long deadlineMs;

    public GrpcUserClient(ManagedChannel userServiceChannel,
                          @Value("${user-service.grpc.deadline-ms:3000}") long deadlineMs) {
        this.stub = UserServiceGrpc.newBlockingStub(userServiceChannel);
        this.deadlineMs = deadlineMs;
    }

    @Override
    public UserInfo getUser(long userId) {
        try {
            GetUserResponse response = stub
                    .withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS)
                    .getUser(GetUserRequest.newBuilder().setUserId(userId).build());
            return new UserInfo(response.getUserId(), response.getName(), response.getEmail());
        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();
            log.warn("gRPC call GetUser({}) failed with status {}: {}",
                    userId, code, e.getStatus().getDescription());
            if (code == Status.Code.NOT_FOUND) {
                throw new UserNotFoundException(userId);
            }
            // UNAVAILABLE (service down), DEADLINE_EXCEEDED (too slow), INTERNAL, ... -> dependency failure
            throw new UserServiceUnavailableException(
                    "user-service call failed with gRPC status " + code, e);
        }
    }
}
