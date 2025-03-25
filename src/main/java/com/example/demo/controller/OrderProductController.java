package com.example.demo.controller;

import com.example.demo.dto.OrderProductDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.service.OrderProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class OrderProductController {

    private final OrderProductService orderProductService;

    public OrderProductController(OrderProductService orderProductService) {
        this.orderProductService = orderProductService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addProductToCart(@RequestBody OrderProductDTO orderProductDTO) {
        orderProductService.addProductsToOrder(orderProductDTO);
        return ResponseEntity.ok("Product added to cart successfully");
    }
    @GetMapping
    public ResponseEntity<List<ProductDTO>> findOrderedProductsByUser(@RequestParam Long userId) {
        List<ProductDTO> products = orderProductService.findOrderedProductsByUser(userId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<List<ProductDTO>> getProductsByOrderId(@PathVariable Long orderId) {
        List<ProductDTO> products = orderProductService.getProductsByOrderId(orderId);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeProductFromCart(@RequestBody OrderProductDTO orderProductDTO) {
        orderProductService.deleteProductFromOrder(orderProductDTO);
        return ResponseEntity.ok("Бараа сагснаас устгагдлаа");
    }
    @DeleteMapping("/delete/all")
    public ResponseEntity<String> deleteProductsByUser(@RequestParam Long userId) {
        orderProductService.deleteProductsByUserId(userId);
        return ResponseEntity.ok("Хэрэглэгчийн бүх бараа амжилттай устгагдлаа");
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