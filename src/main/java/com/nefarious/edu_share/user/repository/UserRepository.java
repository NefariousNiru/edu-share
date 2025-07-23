package com.nefarious.edu_share.user.repository;

import com.nefarious.edu_share.user.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    /**
     * Find a user by email (case-sensitive).
     * @param email the email to look up
     * @return Mono emitting the User if found, or empty if not
     */
    Mono<User> findByEmail(String email);

    /**
     * Find a user by username.
     * @param username the username to look up
     * @return Mono emitting the User if found, or empty if not
     */
    Mono<User> findByUsername(String username);
}

