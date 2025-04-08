package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private UUID id;
    private LocalDateTime orderDate;
    private double totalAmount;
    private UUID userId;
    private List<UUID> productIds;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}