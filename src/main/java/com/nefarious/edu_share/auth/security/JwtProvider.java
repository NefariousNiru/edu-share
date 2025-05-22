package com.nefarious.edu_share.auth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nefarious.edu_share.auth.util.enums.TokenType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
        verifier = JWT.require(algorithm).build();
    }

    /** Generate a signed JWT with {@link TokenType} and subject: user_id */
    public String generateToken(UUID userId, TokenType token) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + (token == TokenType.ACCESS ? accessExpirationMs : refreshExpirationMs));

        return JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .withClaim("type", token.getValue())
                .sign(algorithm);
    }

    /** Validate the token (signature + exp). Returns true if valid */
    public boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    /** Extract the userId from token’s subject claim */
    public UUID getUserIdFromJwt(String token) {
        DecodedJWT decoded = verifier.verify(token);
        String subject = decoded.getSubject();
        return UUID.fromString(subject);
    }

    /** Check that this *is* a refresh token */
    public boolean isRefreshToken(String token) {
        DecodedJWT decoded = verifier.verify(token);
        return "refresh".equals(decoded.getClaim("type").asString());
    }
}
