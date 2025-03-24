package com.example.demo.controller;

import com.example.demo.dto.OrderProductDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.service.OrderProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-products")
public class OrderProductController {

    private final OrderProductService orderProductService;

    public OrderProductController(OrderProductService orderProductService) {
        this.orderProductService = orderProductService;
    }

    @PostMapping
    public ResponseEntity<Void> addProductToOrder(@RequestBody OrderProductDTO orderProductDTO) {
        orderProductService.addProductsToOrder(orderProductDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<List<ProductDTO>> getProductsByOrderId(@PathVariable Long orderId) {
        List<ProductDTO> products = orderProductService.getProductsByOrderId(orderId);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProductFromOrder(@RequestBody OrderProductDTO orderProductDTO) {
        orderProductService.deleteProductFromOrder(orderProductDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Void> updateProductsInOrder(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderProductsRequest request) {
        orderProductService.updateProductsInOrder(orderId, request.getToRemove(), request.getToAdd());
        return ResponseEntity.ok().build();
    }


    private static class UpdateOrderProductsRequest {
        private List<OrderProductDTO> toRemove;
        private List<OrderProductDTO> toAdd;

        public List<OrderProductDTO> getToRemove() {
            return toRemove;
        }

        public void setToRemove(List<OrderProductDTO> toRemove) {
            this.toRemove = toRemove;
        }

        public List<OrderProductDTO> getToAdd() {
            return toAdd;
        }

        public void setToAdd(List<OrderProductDTO> toAdd) {
            this.toAdd = toAdd;
        }
    }
}