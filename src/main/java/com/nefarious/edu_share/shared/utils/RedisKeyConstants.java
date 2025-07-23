package com.nefarious.edu_share.shared.utils;

/**
 * Centralized Redis key constants used across the application.
 * These keys help in structuring and standardizing Redis storage for
 * session management, OTP handling, and rate limiting.
 */
public class RedisKeyConstants {
    /** Prefix for Redis keys tracking login attempts for rate limiting. */
    public static final String SIGNIN = "signin";

    /** Prefix for Redis keys storing one-time passwords (OTP) by email. */
    public static final String OTP = "otp";

    /** Prefix for Redis keys tracking OTP verification attempts for rate limiting. */
    public static final String OTP_VERIFY = "otp_verify";

    /** Prefix for Redis keys tracking forgot-password attempts for rate limiting. */
    public static final String FORGOT_PASSWORD = "forgot-password";

    /** Prefix for Redis keys storing active access tokens (used for validation and revocation). */
    public static final String ACCESS_TOKEN = "access";

    /** Prefix for Redis keys storing active refresh tokens (used for session refresh and revocation). */
    public static final String REFRESH_TOKEN = "refresh";

    /** Prefix for Redis keys mapping a user to all their active session tokens (used for invalidating all sessions). */
    public static final String USER_SESSIONS = "user-sessions";

    /** Prefix for Redis keys used in rate limiting (combined with above types like signin, otp, etc.). */
    public static final String RATE_LIMIT = "rate-limit";
}
