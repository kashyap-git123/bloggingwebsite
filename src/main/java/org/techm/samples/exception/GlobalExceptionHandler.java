package org.techm.samples.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        logger.atWarn()
            .addKeyValue("event.action", "user.registration")
            .addKeyValue("event.outcome", "failure")
            .addKeyValue("error.type", ex.getClass().getName())
            .log("User registration rejected");
        return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        logger.atWarn()
            .addKeyValue("event.action", "user.lookup")
            .addKeyValue("event.outcome", "failure")
            .addKeyValue("error.type", ex.getClass().getName())
            .log("User lookup failed");
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.atError()
            .addKeyValue("event.action", "http.request")
            .addKeyValue("event.outcome", "failure")
            .addKeyValue("error.type", ex.getClass().getName())
            .log("Request failed");
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
