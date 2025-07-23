package com.nefarious.edu_share.shared.service;

import com.nefarious.edu_share.shared.utils.RedisKeyConstants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Duration;

/** Service for rate limiting using Redis */
@Service
@RequiredArgsConstructor
public class RateLimiterService {
    private final ReactiveStringRedisTemplate redis;
    private final Environment env;
    private Duration WINDOW;

    @PostConstruct
    void init() {
        long minutes = Long.parseLong(env.getProperty("rate-limit.cooldown", "15"));
        this.WINDOW = Duration.ofMinutes(minutes);
    }

    /**
     * Attempt to acquire a “permit” for the given key.
     *
     * <p>Each call increments a Redis counter with TTL = WINDOW on first use.
     * Returns Mono<Boolean> indicating whether the count is still ≤ configured limit.
     *
     * @param key           fully‐resolved Redis key (e.g. "signin:foo@bar.com")
     * @param propertyName  property name, e.g. "rate-limit.login-attempts"
     * @return Mono that emits true if under limit, false if exceeded
     */
    public Mono<Boolean> tryAcquire(String key, String propertyName) {
        int limit = Integer.parseInt(env.getProperty(propertyName, "3"));
        String redisKey = RedisKeyConstants.RATE_LIMIT + ":" + key;

        return redis.opsForValue()
                .increment(redisKey)
                .flatMap(count -> {
                    if (count == 1) {
                        return redis.expire(redisKey, WINDOW).thenReturn(true);     // first hit
                    } else if (count <= limit) {
                        return Mono.just(true);                                      // within limit, don't reset TTL
                    } else {
                        return redis.expire(redisKey, WINDOW).thenReturn(false);    // exceeded limit → reset cooldown TTL
                    }
                });
    }

    /**
     * Reset the counter for a given key immediately.
     *
     * @param key fully‐resolved Redis key
     * @return Mono<Void> that completes once deletion is done
     */
    public Mono<Void> reset(String key) {
        String redisKey = RedisKeyConstants.RATE_LIMIT + ":" + key;
        return redis.delete(redisKey).then();
    }
}
