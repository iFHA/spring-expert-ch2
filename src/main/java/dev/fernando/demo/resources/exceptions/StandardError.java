package dev.fernando.demo.resources.exceptions;

import java.time.Instant;

public class StandardError {
    private Integer statusCode;
    private String message;
    private Instant timestamp;
    private String path;
    
    public StandardError() {
    }
    public StandardError(Integer statusCode, String message, Instant timestamp, String path) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }
    public Integer getStatusCode() {
        return statusCode;
    }
    public String getMessage() {
        return message;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public String getPath() {
        return path;
    }
    
}
