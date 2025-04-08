package com.example.demo.util.mapper;

import com.example.demo.dto.OrderProductDTO;
import com.example.demo.entity.OrderProduct;
import com.example.demo.entity.OrderProductId;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class OrderProductMapper {

    public static OrderProductDTO toDto(OrderProduct orderProduct) {
        OrderProductDTO dto = new OrderProductDTO();

        if (orderProduct.getId() != null) {
            dto.setOrderId(orderProduct.getId().getOrderId());
            dto.setProductId(orderProduct.getId().getProductId());
        }

        if (orderProduct.getOrder() != null && orderProduct.getOrder().getUser() != null) {
            dto.setUserId(orderProduct.getOrder().getUser().getId());
        }
        return dto;
    }

    public static OrderProduct toEntity(OrderProductDTO dto) {
        OrderProduct orderProduct = new OrderProduct();

        OrderProductId id = new OrderProductId();
        id.setOrderId(dto.getOrderId());
        id.setProductId(dto.getProductId());

        return orderProduct;
    }

}