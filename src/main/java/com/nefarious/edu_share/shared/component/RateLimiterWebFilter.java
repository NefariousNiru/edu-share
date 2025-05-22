package com.nefarious.edu_share.shared.component;

import com.nefarious.edu_share.shared.annotation.RateLimiter;
import com.nefarious.edu_share.shared.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;


/**
 * A WebFilter that inspects incoming requests for a @RateLimiter annotation
 * on the matched HandlerMethod, performs a Redis‐backed rate check,
 * and either returns 429 or forwards to the handler.
 */
@Component
@RequiredArgsConstructor
public class RateLimiterWebFilter implements WebFilter {
    private RateLimiterService rateLimiterService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Object attr = exchange.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (!(attr instanceof HandlerMethod)) {
            // Skip Rate Limit if not accessing a controller
            return chain.filter(exchange);
        }
        HandlerMethod hm = (HandlerMethod) attr;

        RateLimiter anno = hm.getMethodAnnotation(RateLimiter.class);
        if (anno == null) {
            // Methods that don't have annotation are skipped
            return chain.filter(exchange);
        }

        String key = resolveKey(anno.key(), exchange);

        return rateLimiterService.tryAcquire(key, anno.property())
                .flatMap(ok -> {
                    if (!ok) {
                        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                        return exchange.getResponse().setComplete();
                    }
                    return chain.filter(exchange);
                });
    }

    private String resolveKey(String template, ServerWebExchange exchange) {
        Map<String, String> pathVars =
                exchange.getAttributeOrDefault(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Map.of());
        var queryParams = exchange.getRequest().getQueryParams();

        String resolved = template;
        for (var e : pathVars.entrySet()) {
            resolved = resolved.replace("{" + e.getKey() + "}", e.getValue());
        }
        for (var e : queryParams.entrySet()) {
            resolved = resolved.replace("{" + e.getKey() + "}", e.getValue().getFirst());
        }
        return resolved;
    }
}
