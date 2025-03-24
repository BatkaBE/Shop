package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Product entities.
 */
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Value("${custom.product.max_size}")
    private int maxSize;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Convert ProductDto to Product entity.
     */
    private Product mapToEntity(ProductDTO productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        return product;
    }

    /**
     * Convert Product entity to ProductDto.
     */
    private ProductDTO mapToDto(Product product) {
        return new ProductDTO(product.getId(), product.getName(), product.getPrice(), product.getQuantity());
    }

    /**
     * Save a product with validation.
     */
    public ProductDTO saveProduct(ProductDTO productDto) {
        validateProduct(productDto);
        Product product = mapToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return mapToDto(savedProduct);
    }

    /**
     * Get all the products with a custom max size.
     *
     * @return the list of entities
     */
    public List<ProductDTO> getAllProductsLimited() {
        List<Product> products = productRepository.findAll();

        return products.size() > maxSize ?
                products.stream().limit(maxSize).map(this::mapToDto).toList()
                : products.stream().map(this::mapToDto).toList();
    }

    /**
     * Get a product by ID.
     */
    public Optional<ProductDTO> getProductById(Long id) {

        return productRepository.findById(id).map(this::mapToDto);
    }

    /**
     * Update a product with validation.
     */
    public ProductDTO updateProduct(Long id, ProductDTO productDto) {
        validateProduct(productDto);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (productDto.getName() != null && !productDto.getName().isEmpty()) {
            existingProduct.setName(productDto.getName());
        }
        if (productDto.getPrice() != null && productDto.getPrice() >= 0) {
            existingProduct.setPrice(productDto.getPrice());
        }
        if (productDto.getQuantity() >= 0) {
            existingProduct.setQuantity(productDto.getQuantity());
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToDto(updatedProduct);
    }

    /**
     * Delete a product by ID.
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteById(id);
    }

    /**
     * Manual validation of product DTO.
     */
    private void validateProduct(ProductDTO productDto) {
        if (productDto.getName() == null || productDto.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name is required");
        }
        if (productDto.getPrice() == null || productDto.getPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be non-negative");
        }
        if (productDto.getQuantity() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be non-negative");
        }
    }
}
