package com.example.userservice.grpc;

import com.example.grpc.user.GetUserRequest;
import com.example.grpc.user.GetUserResponse;
import com.example.grpc.user.UserServiceGrpc;
import com.example.userservice.model.User;
import com.example.userservice.repository.InMemoryUserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** gRPC implementation of the UserService defined in user.proto. */
@Component
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(UserGrpcService.class);

    private final InMemoryUserRepository repository;

    public UserGrpcService(InMemoryUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        long userId = request.getUserId();
        log.info("gRPC GetUser(userId={})", userId);

        if (userId <= 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("userId must be a positive number")
                    .asRuntimeException());
            return;
        }

        Optional<User> user = repository.findById(userId);
        if (user.isEmpty()) {
            log.info("User {} not found -> NOT_FOUND", userId);
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("User " + userId + " not found")
                    .asRuntimeException());
            return;
        }

        GetUserResponse response = GetUserResponse.newBuilder()
                .setUserId(user.get().id())
                .setName(user.get().name())
                .setEmail(user.get().email())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
