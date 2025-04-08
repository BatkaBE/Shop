package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private UUID id;
    private String name;
    private double price;
    private int quantity;
    private UUID categoryId;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;

}