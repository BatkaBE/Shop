package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.service.crud.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Create a new product.
     */
    @PostMapping
    public ResponseEntity<ProductDTO> saveProduct(
            @RequestHeader("X-User") String currentUser,
            @RequestBody ProductDTO productDto) {
        ProductDTO newProduct = productService.saveProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    /**
     * Pagination-тэй бүтээгдэхүүн авах
     */
    @GetMapping("/paginated")
    public Page<ProductDTO> getProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getProductsPaginated(page, size);
    }

    /**
     * Filtering-тэй бүтээгдэхүүн авах
     */
    @GetMapping("/filter")
    public List<ProductDTO> getProductsFiltered(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        return productService.getProductsFiltered(name, minPrice, maxPrice);
    }

    /**
     * Get a product by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Optional<ProductDTO>>> getProductById(@PathVariable UUID id) {
        Optional<Optional<ProductDTO>> productDto = Optional.ofNullable(productService.getProductById(id));
        return ResponseEntity.ok(productDto);

    }

    /**
     * Update a product by ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @RequestHeader("X-User") String currentUser,
            @PathVariable UUID id,
            @RequestBody ProductDTO productDto) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDto);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Delete a product by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
