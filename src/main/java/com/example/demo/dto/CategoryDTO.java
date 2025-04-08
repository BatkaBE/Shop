package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
class CategoryDTO {
    private UUID id;
    private String name;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}