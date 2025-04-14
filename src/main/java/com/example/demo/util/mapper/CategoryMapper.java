package com.example.demo.util.mapper;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.entity.Category;

public class CategoryMapper {

    public static CategoryDTO toDto(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setCreatedBy(category.getCreatedBy());
        dto.setUpdatedBy(category.getUpdatedBy());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());

        return dto;
    }

    public static Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setCreatedBy(dto.getCreatedBy());
        category.setUpdatedBy(dto.getUpdatedBy());
        category.setCreatedAt(dto.getCreatedAt());
        category.setUpdatedAt(dto.getUpdatedAt());

        return category;
    }
}
