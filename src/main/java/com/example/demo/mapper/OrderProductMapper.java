package com.example.demo.mapper;

import com.example.demo.dto.OrderProductDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderProduct;
import com.example.demo.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderProductMapper {
    OrderProductMapper INSTANCE = Mappers.getMapper(OrderProductMapper.class);

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    OrderProductDTO toDTO(OrderProduct orderProduct);

    @Mapping(source = "orderId", target = "order.id")
    @Mapping(source = "productId", target = "product.id")
    OrderProduct toEntity(OrderProductDTO dto);
}