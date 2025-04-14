package com.example.demo.service.crud;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entity.Category;
import com.example.demo.exception.error.NotFoundError;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.util.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    // CREATE
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category saved = categoryRepository.save(CategoryMapper.toEntity(categoryDTO));
        return CategoryMapper.toDto(saved);
    }

    // READ ALL
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    // READ BY ID
    public CategoryDTO getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Category not found"));
        return CategoryMapper.toDto(category);
    }

    // UPDATE
    public CategoryDTO updateCategory(UUID id, CategoryDTO updatedDTO) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Category not found"));

        existing.setName(updatedDTO.getName());
        Category updated = categoryRepository.save(existing);
        return CategoryMapper.toDto(updated);
    }

    // DELETE
    public void deleteCategory(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundError("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}
