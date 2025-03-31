package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import com.example.demo.exception.NotFoundError;
import com.example.demo.exception.UniqueFieldError;
import com.example.demo.exception.ValidationError;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * Pagination-тэй бүтээгдэхүүн авах
     */
    public Page<ProductDTO> getProductsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::mapToDto);
    }

    /**
     * Filtering-тэй бүтээгдэхүүн авах
     */
    public List<ProductDTO> getProductsFiltered(String name, Double minPrice, Double maxPrice) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Product probe = new Product();
        if (name != null && !name.isEmpty()) {
            probe.setName(name);
        }

        Example<Product> example = Example.of(probe, matcher);
        List<Product> products = productRepository.findAll(example);

        return products.stream()
                .filter(p -> (minPrice == null || p.getPrice() >= minPrice) &&
                        (maxPrice == null || p.getPrice() <= maxPrice))
                .map(this::mapToDto)
                .collect(Collectors.toList());
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
                .orElseThrow(() -> new NotFoundError("Бараа олдсонгүй"));

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
            throw new UniqueFieldError("id", id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Manual validation of product DTO.
     */
    private void validateProduct(ProductDTO productDto) {
        if (productDto.getName() == null || productDto.getName().isEmpty()) {
            throw new ValidationError("Барааны нэрийг оруулах шаардлагатай\n");
        }
        if (productDto.getPrice() == null || productDto.getPrice() < 0) {
            throw new ValidationError( "Үнэ сөрөг биш байх ёстой");
        }
        if (productDto.getQuantity() < 0) {
            throw new ValidationError("Тоо хэмжээ нь сөрөг биш байх ёстой\n");
        }
    }
}
