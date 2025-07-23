package com.nefarious.edu_share.shared.aspect;

import com.nefarious.edu_share.shared.annotation.RateLimiter;
import com.nefarious.edu_share.shared.exceptions.BaseError;
import com.nefarious.edu_share.shared.exceptions.BusinessException;
import com.nefarious.edu_share.shared.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.env.Environment;
import org.springframework.expression.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.springframework.expression.common.TemplateParserContext;
import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect {

    private final RateLimiterService rateLimiterService;
    private final Environment env;
    private final ExpressionParser parser     = new SpelExpressionParser();
    private final ParserContext    tplContext = new TemplateParserContext("#{", "}");

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint pjp, RateLimiter rateLimiter) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method          method = sig.getMethod();

        // Build the evaluation context
        StandardEvaluationContext ctx = new StandardEvaluationContext();
        String[] names = new DefaultParameterNameDiscoverer()
                .getParameterNames(method);
        Object[] args = pjp.getArgs();
        for (int i = 0; i < Objects.requireNonNull(names).length; i++) {
            ctx.setVariable(names[i], args[i]);
        }

        // Parse "otp:#{#email}" as a template
        String keyTemplate = rateLimiter.key();  // e.g. "otp:#{#email}"
        Expression exp     = parser.parseExpression(keyTemplate, tplContext);
        String     resolvedKey = exp.getValue(ctx, String.class); // "otp:foo@bar.com"

        // Now apply your Redis-backed limiter
        Mono<Boolean> allowed = rateLimiterService.tryAcquire(resolvedKey, rateLimiter.property());

        // Proceed reactively
        Object result = pjp.proceed();
        if (result instanceof Mono) {
            @SuppressWarnings("unchecked")
            Mono<Object> original = (Mono<Object>) result;
            return allowed.flatMap(ok -> ok
                    ? original
                    : Mono.error(new BusinessException(BaseError.TOO_MANY_ATTEMPTS))
            );
        } else {
            // fallback for sync methods
            if (Boolean.FALSE.equals(allowed.block())) {
                throw new BusinessException(BaseError.TOO_MANY_ATTEMPTS);
            }
            return result;
        }
    }
}
