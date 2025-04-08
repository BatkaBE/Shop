package com.example.demo.exception.error;

public class NotFoundError extends RuntimeException {
    public NotFoundError(String message) {
        super(message);
    }
}