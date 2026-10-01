package org.techm.samples.aspect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class LoggingAspectTest {

    private final Logger logger = (Logger) LoggerFactory.getLogger(LoggingAspect.class);
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    @BeforeEach
    void attachAppender() {
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        logger.detachAppender(appender);
        appender.stop();
    }

    @Test
    void doesNotLogArgumentsOrReturnedValues() throws Throwable {
        String sensitiveValue = "demo-private-value@example.test";
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("handleRequest");
        when(signature.getDeclaringTypeName()).thenReturn("sample.Controller");
        when(joinPoint.proceed()).thenReturn(sensitiveValue);

        Object result = new LoggingAspect().logApplicationCall(joinPoint);

        assertEquals(sensitiveValue, result);
        assertFalse(appender.list.get(0).getFormattedMessage().contains(sensitiveValue));
        assertEquals("Application method completed", appender.list.get(0).getFormattedMessage());
    }

    @Test
    void doesNotLogExceptionMessages() throws Throwable {
        String sensitiveValue = "demo-private-value@example.test";
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("handleRequest");
        when(signature.getDeclaringTypeName()).thenReturn("sample.Controller");
        when(joinPoint.proceed()).thenThrow(new IllegalArgumentException(sensitiveValue));

        assertThrows(IllegalArgumentException.class, () -> new LoggingAspect().logApplicationCall(joinPoint));

        assertFalse(appender.list.get(0).getFormattedMessage().contains(sensitiveValue));
        assertEquals("Application method failed", appender.list.get(0).getFormattedMessage());
    }
}