package com.example.demo.exception;

public class OrderProductNotFoundException extends RuntimeException {
    public OrderProductNotFoundException(Long orderId, Long productId) {
        super("OrderProduct not found for order ID " + orderId + " and product ID " + productId);
    }
}