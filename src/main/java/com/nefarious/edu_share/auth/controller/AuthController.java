package com.nefarious.edu_share.auth.controller;

import com.nefarious.edu_share.auth.dto.*;
import com.nefarious.edu_share.auth.service.AuthService;
import com.nefarious.edu_share.auth.util.Endpoint;
import com.nefarious.edu_share.shared.annotation.RateLimiter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(Endpoint.AUTH)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Handles user signup requests.
     *
     * <p>Accepts a JSON payload containing user registration information,
     * validates it, and delegates user creation to the {@link AuthService}.
     *
     * @param signupRequest {@link SignupRequest} the signup request data containing email, password, username, etc.
     * @return 202 Accepted if signup request is successfully processed
     */
    @PostMapping(Endpoint.SIGNUP)
    public Mono<Void> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.signup(signupRequest);
    }

    /**
     * Handles user signin requests.
     *
     * <p>Accepts a JSON payload containing user credentials,
     * authenticates the user, and returns a session with access and refresh tokens.
     *
     * @param signinRequest {@link SigninRequest} containing email and password.
     * @return 200 OK with {@link TokenPair} containing tokens if authentication succeeds.
     */
    @RateLimiter(key = "signin:#{#signinRequest.email}", property = "rate-limit.login-attempts")
    @PostMapping(Endpoint.SIGNIN)
    public Mono<TokenPair> signin(@Valid @RequestBody SigninRequest signinRequest) {
        return authService.signin(signinRequest);
    }

    /**
     * Handles OTP verification requests during signup.
     *
     * <p>Accepts a JSON payload with email and OTP code,
     * verifies the OTP, marks the user's email as verified,
     * and generates session tokens.
     *
     * @param request {@link OtpVerificationRequest} containing email and OTP code.
     * @return 200 OK with {@link TokenPair} containing access and refresh tokens upon successful verification.
     */
    @RateLimiter(key = "otp:#{#request.email}", property = "rate-limit.refresh-attempts")
    @PostMapping(Endpoint.VERIFY_OTP)
    public Mono<TokenPair> verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
        return authService.verifyOtp(request.getEmail(), request.getCode());
    }

    /**
     * Handles requests to resend OTP during signup.
     *
     * <p>Accepts an email as a request parameter,
     * generates a new OTP, saves it, and sends it to the user's email address.
     *
     * @param email the email address to which the OTP will be sent. Must be a valid email.
     * @return 200 Accepted if the OTP is successfully generated and sent.
     */
    @RateLimiter(key = "otp:#{#email}", property = "rate-limit.otp-attempts")
    @GetMapping(Endpoint.SEND_OTP)
    public Mono<Void> sendOtp(@RequestParam @Email String email) {
        return authService.sendOtp(email);
    }

    /**
     * Handles session refresh requests.
     *
     * <p>Accepts a JSON payload containing a valid access and refresh token pair,
     * validates the refresh token, revokes the old session, and issues a new access and refresh token pair.
     *
     * @param tokenPair the {@link TokenPair} containing the current access and refresh tokens.
     * @return a {@link Mono} emitting a new {@link TokenPair} if the refresh is successful.
     */
    @RateLimiter(key = "refresh:#{#tokenPair.refreshToken}", property = "rate-limit.refresh-attempts")
    @PostMapping(Endpoint.REFRESH_SESSION)
    public Mono<TokenPair> refreshSession(@Valid @RequestBody TokenPair tokenPair) {
        return authService.refreshSession(tokenPair);
    }

    /**
     * Logs out the user by invalidating the provided access and refresh tokens.
     *
     * <p>Accepts a JSON payload with both tokens and removes the associated sessions
     * from the session store, effectively logging the user out.
     *
     * @param tokenPair the {@link TokenPair} representing the session to invalidate.
     * @return a {@link Mono<Void>} indicating completion of the logout process.
     */
    @PostMapping(Endpoint.LOGOUT)
    public Mono<Void> logout(@Valid @RequestBody TokenPair tokenPair) {
        return authService.logout(tokenPair);
    }

    /**
     * Handles password reset requests after OTP verification.
     *
     * <p>Accepts a JSON payload with email, OTP code, and new password.
     * If the OTP is valid, the user's password is updated, all sessions are invalidated,
     * and a new session with fresh tokens is created.
     *
     * @param request the {@link ForgotPasswordRequest} containing the email, OTP, and new password.
     * @return a {@link Mono} emitting a new {@link TokenPair} upon successful password reset.
     */
    @RateLimiter(key = "forgot-password:#{#request.email}", property = "rate-limit.refresh-attempts")
    @PostMapping(Endpoint.FORGOT_PASSWORD)
    public Mono<TokenPair> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authService.forgotPassword(request);
    }
}
