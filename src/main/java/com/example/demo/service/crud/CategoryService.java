package com.example.demo.service.crud;

import com.example.demo.entity.Category;
import com.example.demo.exception.error.NotFoundError;
import com.example.demo.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    // READ ALL
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // READ BY ID
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Category not found"));
    }

    // UPDATE
    public Category updateCategory(UUID id, Category updatedCategory) {
        Category existing = getCategoryById(id);
        existing.setName(updatedCategory.getName());
        return categoryRepository.save(existing);
    }

    // DELETE
    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }
}