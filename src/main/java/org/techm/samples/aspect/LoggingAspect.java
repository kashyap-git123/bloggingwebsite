package org.techm.samples.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.techm.samples.filter.RequestCorrelationFilter;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* org.techm.samples.service..*(..)) || execution(* org.techm.samples.controller..*(..))")
    public void applicationLayer() {}

    @Around("applicationLayer()")
    public Object logApplicationCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            String correlationId = MDC.get(RequestCorrelationFilter.REQUEST_ID_MDC_KEY);
            var eventBuilder = logger.atInfo()
                    .addKeyValue("event.action", "application.method")
                    .addKeyValue("event.outcome", "success")
                    .addKeyValue("code.function", joinPoint.getSignature().getName())
                    .addKeyValue("code.namespace", joinPoint.getSignature().getDeclaringTypeName())
                    .addKeyValue("event.duration", System.nanoTime() - startedAt);
            if (correlationId != null) {
                eventBuilder = eventBuilder.addKeyValue("correlation.id", correlationId);
            }
            eventBuilder.log("Application method completed");
            return result;
        } catch (Throwable exception) {
            String correlationId = MDC.get(RequestCorrelationFilter.REQUEST_ID_MDC_KEY);
            var eventBuilder = logger.atError()
                    .addKeyValue("event.action", "application.method")
                    .addKeyValue("event.outcome", "failure")
                    .addKeyValue("code.function", joinPoint.getSignature().getName())
                    .addKeyValue("code.namespace", joinPoint.getSignature().getDeclaringTypeName())
                    .addKeyValue("error.type", exception.getClass().getName())
                    .addKeyValue("error.message", safeMessage(exception))
                    .addKeyValue("event.duration", System.nanoTime() - startedAt);
            if (correlationId != null) {
                eventBuilder = eventBuilder.addKeyValue("correlation.id", correlationId);
            }
            eventBuilder.log("Application method failed");
            throw exception;
        }
    }

    private static String safeMessage(Throwable throwable) {
        return throwable.getMessage() == null ? throwable.getClass().getSimpleName() : throwable.getMessage();
    }
}

