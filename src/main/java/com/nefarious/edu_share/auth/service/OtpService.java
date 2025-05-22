package com.nefarious.edu_share.auth.service;

import com.nefarious.edu_share.shared.utils.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.Duration;

/** Service for generating, storing, and validating one-time passwords (OTPs) in Redis */
@Service
@RequiredArgsConstructor
public class OtpService {
    private final ReactiveStringRedisTemplate redis;
    private final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 10;
    @Value("${otp-ttl}")
    private int otpTtl;

    /**
     * Generate a 10-character alphanumeric OTP, store it in Redis
     * under the key "otp:{email}" with a TTL, and return it.
     *
     * @param email the identifier for this OTP (e.g. user’s email)
     * @return a Mono emitting the generated OTP string
     */
    public Mono<String> getOtp(String email) {
        String otp = generateOtp();
        String key = buildKey(email);
        return redis
                .opsForValue()
                .set(key, otp, Duration.ofSeconds(otpTtl))
                .thenReturn(otp);
    }

    /**
     * Validate a submitted OTP against the stored value in Redis.
     * If it matches, delete the key so it cannot be reused.
     *
     * @param email the identifier under which the OTP was stored
     * @param submittedOtp the OTP provided by the user
     * @return a Mono emitting true if valid (and deleted), false otherwise
     */
    public Mono<Boolean> validateOtp(String email, String submittedOtp) {
        String key = buildKey(email);
        return redis
                .opsForValue()
                .get(key)
                .flatMap(storedOtp -> {
                    if (storedOtp.equals(submittedOtp)) {
                        // correct → delete the key
                        return redis
                                .delete(key)
                                .thenReturn(true);
                    }
                    return Mono.just(false);
                })
                .defaultIfEmpty(false);
    }

    /** Generate a random alphanumeric string of length OTP_LENGTH. */
    private String generateOtp() {
        var sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int idx = random.nextInt(Constant.ALPHANUMERIC.length());
            sb.append(Constant.ALPHANUMERIC.charAt(idx));
        }
        return sb.toString();
    }

    /** Helper to build the Redis key for a given email. */
    private String buildKey(String email) {
        return "otp:" + email;
    }
}
