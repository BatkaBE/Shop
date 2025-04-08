package com.example.demo.util.mapper;

import com.example.demo.dto.OrderDTO;
import com.example.demo.entity.Order;

import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDTO toDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
        }

        if (order.getOrderProducts() != null) {
            dto.setProductIds(order.getOrderProducts().stream()
                    .map(orderProduct -> orderProduct.getProduct().getId())
                    .collect(Collectors.toList()));
        }

        // Audit fields
        dto.setCreatedBy(order.getCreatedBy());
        dto.setUpdatedBy(order.getUpdatedBy());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        return dto;
    }

    public static Order toEntity(OrderDTO dto) {
        Order order = new Order();
        order.setId(dto.getId());
        order.setOrderDate(dto.getOrderDate());
        order.setTotalAmount(dto.getTotalAmount());
        // Note: user and orderProducts must be set separately in service layer
        return order;
    }
}