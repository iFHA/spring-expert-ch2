package dev.fernando.demo.resources.exceptions;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.fernando.demo.services.exceptions.DatabaseException;
import dev.fernando.demo.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        int status = HttpStatus.NOT_FOUND.value();
        String path = request.getRequestURI();
        return ResponseEntity.status(status).body(new StandardError(status, e.getMessage(), Instant.now(), path));
    }
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> handleDatabaseException(DatabaseException e, HttpServletRequest request) {
        int status = HttpStatus.BAD_REQUEST.value();
        String path = request.getRequestURI();
        return ResponseEntity.status(status).body(new StandardError(status, e.getMessage(), Instant.now(), path));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError validationError = new ValidationError(status.value(), ex.getMessage(), Instant.now(), request.getRequestURI());
        ex.getBindingResult()
        .getFieldErrors()
        .forEach(validationError::addFieldMessage);
        return ResponseEntity.status(status).body(validationError);
    }
}
