package org.techm.samples.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.techm.samples.filter.RequestCorrelationFilter;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException ex,
                                                         HttpServletRequest request,
                                                         HttpServletResponse response) {
        HttpStatus status = HttpStatus.CONFLICT;
        response.setStatus(status.value());
        String correlationId = MDC.get(RequestCorrelationFilter.REQUEST_ID_MDC_KEY);
        var eventBuilder = logger.atWarn()
            .addKeyValue("event.action", "user.registration")
            .addKeyValue("event.outcome", "failure")
            .addKeyValue("error.type", ex.getClass().getName())
            .addKeyValue("error.message", safeMessage(ex))
            .addKeyValue("http.request.method", request.getMethod())
            .addKeyValue("url.path", request.getRequestURI())
            .addKeyValue("url.template", request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
            .addKeyValue("http.response.status_code", status.value());
        if (correlationId != null) {
            eventBuilder = eventBuilder.addKeyValue("correlation.id", correlationId);
        }
        eventBuilder.log("User registration rejected");
        return new ResponseEntity<>("User already exists", status);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        response.setStatus(status.value());
        String correlationId = MDC.get(RequestCorrelationFilter.REQUEST_ID_MDC_KEY);
        var eventBuilder = logger.atWarn()
            .addKeyValue("event.action", "user.lookup")
            .addKeyValue("event.outcome", "failure")
            .addKeyValue("error.type", ex.getClass().getName())
            .addKeyValue("error.message", safeMessage(ex))
            .addKeyValue("http.request.method", request.getMethod())
            .addKeyValue("url.path", request.getRequestURI())
            .addKeyValue("url.template", request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
            .addKeyValue("http.response.status_code", status.value());
        if (correlationId != null) {
            eventBuilder = eventBuilder.addKeyValue("correlation.id", correlationId);
        }
        eventBuilder.log("User lookup failed");
        return new ResponseEntity<>("User not found", status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        response.setStatus(status.value());
        String correlationId = MDC.get(RequestCorrelationFilter.REQUEST_ID_MDC_KEY);
        var eventBuilder = logger.atError()
            .addKeyValue("event.action", "http.request")
            .addKeyValue("event.outcome", "failure")
            .addKeyValue("error.type", ex.getClass().getName())
            .addKeyValue("error.message", safeMessage(ex))
            .addKeyValue("http.request.method", request.getMethod())
            .addKeyValue("url.path", request.getRequestURI())
            .addKeyValue("url.template", request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE))
            .addKeyValue("http.response.status_code", status.value());
        if (correlationId != null) {
            eventBuilder = eventBuilder.addKeyValue("correlation.id", correlationId);
        }
        eventBuilder.log("Request failed");
        return new ResponseEntity<>("An unexpected error occurred", status);
    }

    private static String safeMessage(Throwable throwable) {
        return throwable.getMessage() == null ? throwable.getClass().getSimpleName() : throwable.getMessage();
    }
}
