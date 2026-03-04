package com.nefarious.edu_share.auth.security;

import com.nefarious.edu_share.auth.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Custom authentication filter that processes Bearer tokens in the Authorization header.
 * Validates the token using the {@link SessionService} and sets the authentication context if valid.
 */
@RequiredArgsConstructor
public class AuthTokenBearer implements WebFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private final SessionService sessionService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String header = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // No header (or not Bearer) - do not authenticate, just continue.
        // Public routes will work; protected routes will fail later at `.authenticated()`.
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            return Mono.error(new BadCredentialsException("Empty Bearer token"));
        }

        return sessionService.validateAccessToken(token)
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid token")))
                .flatMap(userId -> {
                    var auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                });
    }
}
