package org.techm.samples.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
            logger.atInfo()
                    .addKeyValue("event.action", "application.method")
                    .addKeyValue("event.outcome", "success")
                    .addKeyValue("code.function", joinPoint.getSignature().getName())
                    .addKeyValue("code.namespace", joinPoint.getSignature().getDeclaringTypeName())
                    .addKeyValue("event.duration", System.nanoTime() - startedAt)
                    .log("Application method completed");
            return result;
        } catch (Throwable exception) {
            logger.atError()
                    .addKeyValue("event.action", "application.method")
                    .addKeyValue("event.outcome", "failure")
                    .addKeyValue("code.function", joinPoint.getSignature().getName())
                    .addKeyValue("code.namespace", joinPoint.getSignature().getDeclaringTypeName())
                    .addKeyValue("error.type", exception.getClass().getName())
                    .addKeyValue("event.duration", System.nanoTime() - startedAt)
                    .log("Application method failed");
            throw exception;
        }
    }
}

