package com.nefarious.edu_share.shared.annotation;

import java.lang.annotation.*;

/**
 * Custom annotation to apply rate limiting to controller-only.
 *
 * <p>Usage example:
 * <pre>
 *     @RateLimiter(key = "signin:{email}", property = "rate-limit.login-attempts")
 *     public void login(String email) { ... }
 * </pre>
 *
 * @param key Redis key template. Use placeholders like {email} to be replaced by method arguments.
 * @param property Application property name that specifies the rate limit threshold.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    /** Redis key template. Use placeholders like {email} */
    String key();
    /** Property name in application properties, e.g. "rate-limit.login-attempts" */
    String property();
}
