package com.example.demo.exception.error;

public class ValidationError extends RuntimeException {
    public ValidationError(String message) {
        super(message);
    }
}