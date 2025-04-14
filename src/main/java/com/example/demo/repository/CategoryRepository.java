package com.example.demo.repository;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Category findByName(String name);
}