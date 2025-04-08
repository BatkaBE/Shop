package com.example.demo.controller;

import com.example.demo.dto.OrderDTO;
import com.example.demo.service.crud.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** Create order */
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @RequestHeader("X-User") String currentUser,
            @RequestBody OrderDTO dto
    ) {
        OrderDTO created = orderService.createOrder(currentUser, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /** Get all */
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /** Get by ID */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /** Update order */
    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(
            @RequestHeader("X-User") String currentUser,
            @PathVariable UUID id,
            @RequestBody OrderDTO dto
    ) {
        OrderDTO updated = orderService.updateOrder(currentUser, id, dto);
        return ResponseEntity.ok(updated);
    }

    /** Delete */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        orderService.deleteOrder(id);
    }
}
