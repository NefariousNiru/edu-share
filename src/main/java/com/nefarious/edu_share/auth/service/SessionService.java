package com.nefarious.edu_share.auth.service;

import com.nefarious.edu_share.auth.security.JwtProvider;
import com.nefarious.edu_share.auth.util.enums.TokenType;
import com.nefarious.edu_share.shared.utils.RedisKeyConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final ReactiveStringRedisTemplate redis;
    private final JwtProvider jwtProvider;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    /** Store a session token in Redis (reactive). */
    public Mono<Void> createSession(String token, UUID userId, TokenType tokenType) {
        String key = tokenType.getValue() + ":" + token;     // tokenType.getValue() resolves to 'access' or 'refresh' redis keys
        long ttl = tokenType == TokenType.ACCESS ? accessExpirationMs : refreshExpirationMs;

        Mono<Boolean> storeToken = redis
                .opsForValue()
                .set(key, userId.toString(), Duration.ofMillis(ttl));

        String userSessionKey = RedisKeyConstants.USER_SESSIONS + ":" + userId;          // For reverse lookups
        Mono<Long> pushToList = redis
                .opsForList()
                .rightPush(userSessionKey, key);

        return Mono.when(storeToken, pushToList).then();
    }

    /** Validate an access token; returns the userId if valid. */
    public Mono<UUID> validateAccessToken(String token) {
        return validateSession(token, TokenType.ACCESS);
    }

    /** Validate a refresh token; returns the userId if valid. */
    public Mono<UUID> validateRefreshToken(String token) {
        return validateSession(token, TokenType.REFRESH);
    }

    /** Invalidate all sessions for a user. */
    public Mono<Void> invalidateAllSessionsForUser(UUID userId) {
        String userSessionKey = RedisKeyConstants.USER_SESSIONS + ":" + userId;
        return redis
                .opsForList()
                .range(userSessionKey, 0, -1)
                .flatMap(redis::delete)
                .then(redis.delete(userSessionKey))
                .then();
    }

    /** Revoke a single token. */
    public Mono<Void> revokeSession(String token, TokenType tokenType) {
        String key = tokenType.getValue() + ":" + token;
        return redis.delete(key).then();
    }

    /** Core validation: check JWT then verify presence in Redis. */
    private Mono<UUID> validateSession(String token, TokenType type) {
        if (!jwtProvider.validateToken(token)) {
            return Mono.empty();
        }
        if (type == TokenType.REFRESH && !jwtProvider.isRefreshToken(token)) {
            return Mono.empty();
        }
        String key = type.getValue() + ":" + token;
        return redis
                .opsForValue()
                .get(key)
                .filter(uidStr -> {
                    UUID tokenUid = jwtProvider.getUserIdFromJwt(token);
                    return tokenUid.toString().equals(uidStr);
                })
                .map(uidStr -> jwtProvider.getUserIdFromJwt(token));
    }
}
