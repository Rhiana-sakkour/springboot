package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

/** Read-only in-memory user store seeded with a few demo users. */
@Repository
public class InMemoryUserRepository {

    private final Map<Long, User> users = Map.of(
            101L, new User(101L, "Ahmed", "ahmed@example.com"),
            102L, new User(102L, "Sara", "sara@example.com"),
            103L, new User(103L, "Omar", "omar@example.com"));

    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }
}
