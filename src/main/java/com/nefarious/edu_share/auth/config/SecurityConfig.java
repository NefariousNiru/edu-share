package com.nefarious.edu_share.auth.config;

import com.nefarious.edu_share.auth.security.AuthTokenBearer;
import com.nefarious.edu_share.auth.service.SessionService;
import com.nefarious.edu_share.auth.util.Endpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
public class SecurityConfig {
    /**
     * Defines a {@link PasswordEncoder} bean using BCrypt hashing algorithm.
     * @return a new {@link BCryptPasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines a {@link AuthTokenBearer} bean used to validate and authenticate bearer tokens.
     * @param sessionService {@link SessionService} service to validate user sessions
     * @return a new {@link AuthTokenBearer} instance
     */
    @Bean
    public AuthTokenBearer authTokenBearer(SessionService sessionService) {
        return new AuthTokenBearer(sessionService);
    }

    /**
     * Configures the security filter chain for the application.
     * <p>
     * Disables CSRF protection and HTTP Basic/Form login, uses a no-op security context,
     * permits unauthenticated access to Swagger UI, API docs, and authentication endpoints,
     * and applies a custom bearer token authentication filter to all other requests.
     *
     * @param http             the ServerHttpSecurity to configure
     * @param authTokenBearer  custom authentication filter for bearer tokens
     * @return a built SecurityWebFilterChain enforcing the above rules
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthTokenBearer authTokenBearer) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        http.authorizeExchange(exchanges -> exchanges
                .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", Endpoint.AUTH + "/**")
                .permitAll()
                .anyExchange()
                .authenticated()
        );
        http.addFilterAt(authTokenBearer, SecurityWebFiltersOrder.AUTHENTICATION);
        http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
        http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);
        return http.build();
    }
}
