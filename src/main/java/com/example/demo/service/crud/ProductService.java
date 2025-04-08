package com.example.demo.service.crud;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import com.example.demo.exception.error.NotFoundError;
import com.example.demo.exception.error.UniqueFieldError;
import com.example.demo.exception.error.ValidationError;
import com.example.demo.repository.ProductRepository;
import com.example.demo.util.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing Product entities.
 */
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Value("${custom.product.max_size}")
    private int maxSize;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    /**
     * Save a product with validation (requires createdBy).
     */
    public ProductDTO saveProduct(ProductDTO dto) {
        validateProduct(dto);
        if (dto.getCreatedBy() == null || dto.getCreatedBy().trim().isEmpty()) {
            throw new ValidationError("createdBy cannot be empty");
        }
        Product saved = productRepository.save(ProductMapper.toEntity(dto));
        return ProductMapper.toDto(saved);
    }

    /**
     * Pagination‑enabled listing
     */
    public Page<ProductDTO> getProductsPaginated(int page, int size) {
        Pageable pg = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findAll(pg).map(ProductMapper::toDto);
    }

    /**
     * Filtering‑enabled listing
     */
    public List<ProductDTO> getProductsFiltered(String name, Double minPrice, Double maxPrice) {
        ExampleMatcher m = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Product probe = new Product();
        if (name != null && !name.isBlank()) probe.setName(name);

        List<Product> list = productRepository.findAll(Example.of(probe, m));
        return list.stream()
                .filter(p -> (minPrice == null || p.getPrice() >= minPrice)
                        && (maxPrice == null || p.getPrice() <= maxPrice))
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get a product by ID.
     */
    public Optional<ProductDTO> getProductsById(UUID id) {
        return productRepository.findById(id).map(ProductMapper::toDto);
    }

    /**
     * Update a product with validation (requires updatedBy).
     */
    public ProductDTO updateProduct(UUID id, ProductDTO dto) {
        validateProduct(dto);
        if (dto.getUpdatedBy() == null || dto.getUpdatedBy().trim().isEmpty()) {
            throw new ValidationError("updatedBy cannot be empty");
        }

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Product not found"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            existing.setName(dto.getName());
        }
        if (dto.getQuantity() >= 0) {
            existing.setQuantity(dto.getQuantity());
        }
        // wire in audit
        existing.setUpdatedBy(dto.getUpdatedBy());

        Product updated = productRepository.save(existing);
        return ProductMapper.toDto(updated);
    }

    /**
     * Delete a product by ID.
     */
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new UniqueFieldError("id", id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Manual validation of name/quantity.
     */
    private void validateProduct(ProductDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ValidationError("Барааны нэрийг оруулах шаардлагатай");
        }
        if (dto.getQuantity() < 0) {
            throw new ValidationError("Тоо хэмжээ нь сөрөг биш байх ёстой");
        }
    }

    public Optional<ProductDTO> getProductById(UUID id) {
        return null;
    }
}
