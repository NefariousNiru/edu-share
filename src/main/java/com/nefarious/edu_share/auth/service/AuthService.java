package com.nefarious.edu_share.auth.service;

import com.nefarious.edu_share.auth.dto.OtpVerificationRequest;
import com.nefarious.edu_share.auth.dto.SigninRequest;
import com.nefarious.edu_share.auth.dto.SignupRequest;
import com.nefarious.edu_share.auth.dto.TokenPair;
import com.nefarious.edu_share.auth.security.JwtProvider;
import com.nefarious.edu_share.auth.util.enums.AuthError;
import com.nefarious.edu_share.auth.util.enums.TokenType;
import com.nefarious.edu_share.shared.exceptions.BusinessException;
import com.nefarious.edu_share.shared.interfaces.EmailService;
import com.nefarious.edu_share.user.entity.User;
import com.nefarious.edu_share.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserService userService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;
    private final SessionService sessionService;

    /**
     * Handles the user signup process.
     * <p>
     * This method creates a new user, generates an OTP, and sends it to the user's email.
     * If email sending fails, it logs the error without interrupting the signup process.
     *
     * @param request {@link SignupRequest} The signup request containing user information.
     */
    public Mono<Void> signup(@Valid SignupRequest request) {
        // TODO: Add A new Neo4j pub sub to alert new user and add node
        // TODO:Also think about device specific OTP, if we send the otp as a response we send a temp code to the browser as well.
        // When user responds they respond with the otp and the browser code attached, so i know that this request came from the same guy
        return userService.createUser(request)
            .flatMap(user ->
                this.sendOtp(request.getEmail())
                    .onErrorResume(e -> {
                        log.warn("OTP email failed for {}: {}", request.getEmail(), e.getMessage());
                        return Mono.empty();
                    })
            );
    }

    /**
     * Handles user signin requests. Adds a RateLimit AOP to limit queries to this window
     *
     * <p>Authenticates the user credentials, and upon successful authentication,
     * generates access and refresh tokens and creates a session.
     *
     * @param request {@link SigninRequest} containing email and password.
     * @return {@link TokenPair} containing access and refresh tokens upon successful signin.
     */
    public Mono<TokenPair> signin(SigninRequest request) {
        return userService.authenticate(request)
            .flatMap(user -> this.createSession(user.getId()));
    }

    /**
     * Generates a new OTP for the given email, saves it, and sends it via email. Rate-limited using RateLimit AOP.
     *
     * <p>Creates a one-time password (OTP), stores it for verification,
     * and emails the OTP to the provided address.
     *
     * @param email the email address to which the OTP will be sent.
     */
    public Mono<Void> sendOtp(String email) {
        return otpService.getOtp(email)
            .flatMap(code -> emailService.sendOtpEmail(email, code));
    }

    public Mono<TokenPair> verifyOtp(String email, String code) {
        return otpService.validateOtp(email, code)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new BusinessException(AuthError.INVALID_OTP)))
                .flatMap(valid -> userService.markEmailVerified(email))
                .flatMap(user -> createSession(user.getId()));
    }

    /**
     * Generate a token using {@link JwtProvider} and create a session using {@link SessionService} (persist in Redis)
     * @param userId userId of user to create token against
     * @return sessionResponse {@link TokenPair} type object
     */
    private Mono<TokenPair> createSession(UUID userId) {
        String accessToken  = jwtProvider.generateToken(userId, TokenType.ACCESS);
        String refreshToken = jwtProvider.generateToken(userId, TokenType.REFRESH);

        return Mono.when(
                        sessionService.createSession(accessToken, userId, TokenType.ACCESS),
                        sessionService.createSession(refreshToken, userId, TokenType.REFRESH)
                )
                .thenReturn(new TokenPair(accessToken, refreshToken));
    }
}
