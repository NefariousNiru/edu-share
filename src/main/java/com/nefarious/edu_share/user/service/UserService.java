package com.nefarious.edu_share.user.service;

import com.nefarious.edu_share.auth.dto.SigninRequest;
import com.nefarious.edu_share.auth.dto.SignupRequest;
import com.nefarious.edu_share.auth.util.enums.AuthError;
import com.nefarious.edu_share.shared.exceptions.BusinessException;
import com.nefarious.edu_share.user.entity.User;
import com.nefarious.edu_share.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates and persists a new User if email and username are available.
     *
     * @param request {@link SignupRequest} the signup request containing user input data
     * @return Mono emitting the created User or error if validation fails
     */
    public Mono<User> createUser(SignupRequest request) {
        return checkIfEmailOrUsernameTaken(request)
            .then(Mono.defer(() -> encodePassword(request.getPassword())))
            .map(encodedPwd -> buildUser(request, encodedPwd))
            .flatMap(userRepository::save);
    }

    /**
     * Authenticates a user by verifying email and password credentials.
     * <p>Throws {@link BusinessException} if credentials are invalid or email is not verified.
     * @param request {@link SigninRequest} containing the user's email and password.
     */
    public Mono<User> authenticate(SigninRequest request) {
        return getByEmail(request.getEmail())
            .switchIfEmpty(Mono.error(new BusinessException(AuthError.INVALID_CREDENTIALS)))
            .flatMap(user -> {
                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                    return Mono.error(new BusinessException(AuthError.INVALID_CREDENTIALS));
                }
                if (!user.isEmailVerified()) {
                    return Mono.error(new BusinessException(AuthError.EMAIL_NOT_VERIFIED));
                }
                return Mono.just(user);
            });
    }

    /**
     * Marks the user's email as verified.
     * <p>Fetches the user by email, updates the verification status, and persists the change.
     * @param email the email address of the user to mark as verified.
     */
    public Mono<User> markEmailVerified(String email) {
        return this.getByEmail(email)
            .flatMap(user -> {
                user.setEmailVerified(true);
                return userRepository.save(user);
            });
    }

    /**
     * Updates a user's password
     * @param email Email of user
     * @param rawPassword Plaintext password of user
     */
    public Mono<User> updatePassword(String email, String rawPassword) {
        return this.getByEmail(email)
            .flatMap(user -> encodePassword(rawPassword)
                .map(hashed -> {
                    user.setPassword(hashed);
                    return user;
                }))
            .flatMap(userRepository::save);
    }

    /**
     * Retrieves a user by email.
     * <p>Throws {@link BusinessException} if no user is found with the given email.
     * @param email the email address to search for.
     * @return Mono {@link User} entity matching the provided email.
     */
    public Mono<User> getByEmail(String email) {
        return userRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(new BusinessException(AuthError.USER_NOT_EXISTS)));
    }

    /**
     * Validates that the email and username are not already in use.
     * Emits an error Mono if either is taken.
     * @param req the signup request containing the email and username
     * @return Mono.empty() if both are free, otherwise error
     */
    private Mono<Void> checkIfEmailOrUsernameTaken(SignupRequest req) {
        return Mono.zip(
            userRepository.findByEmail(req.getEmail()).hasElement(),
            userRepository.findByUsername(req.getUsername()).hasElement()
        ).flatMap(tuple -> {
            if (tuple.getT1()) return Mono.error(new BusinessException(AuthError.EMAIL_IN_USE));
            if (tuple.getT2()) return Mono.error(new BusinessException(AuthError.USERNAME_IN_USE));
            return Mono.empty();
        });
    }

    /**
     * Encodes the raw password on a boundedElastic scheduler (off the event loop).
     * @param rawPassword the plaintext password
     * @return Mono emitting the encoded password
     */
    private Mono<String> encodePassword(String rawPassword) {
        return Mono.fromCallable(() -> passwordEncoder.encode(rawPassword))
            .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Builds a User entity from the signup request and the encoded password.
     * @param req           the signup request with user input data
     * @param encodedPassword the hashed password
     * @return a new User object ready to persist
     */
    private User buildUser(SignupRequest req, String encodedPassword) {
        return User.builder()
                .email(req.getEmail())
                .username(req.getUsername())
                .password(encodedPassword)
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .dateOfBirth(req.getDateOfBirth())
                .isEmailVerified(false)
                .build();
    }
}
