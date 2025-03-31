package com.example.demo.exception;

public class NotFoundError extends RuntimeException {
    public NotFoundError(String message) {
        super(message);
    }
}