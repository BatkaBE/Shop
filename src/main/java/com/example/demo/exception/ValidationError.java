package com.example.demo.exception;

public class ValidationError extends RuntimeException {
    public ValidationError(String message) {
        super(message);
    }
}