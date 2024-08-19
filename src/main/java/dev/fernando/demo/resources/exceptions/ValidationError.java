package dev.fernando.demo.resources.exceptions;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.validation.FieldError;

public class ValidationError extends StandardError implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<FieldMessage> errors = new ArrayList<>();
    public ValidationError(Integer statusCode, String message, Instant timestamp, String path) {
        super(statusCode, message, timestamp, path);
    }

    public List<FieldMessage> getErrors() {
        return Collections.unmodifiableList(errors);
    }
    public void addFieldMessage(String fieldName, String message) {
        errors.add(new FieldMessage(fieldName, message));
    }
    public void addFieldMessage(FieldError error) {
        errors.add(new FieldMessage(error.getField(), error.getDefaultMessage()));
    }
}
