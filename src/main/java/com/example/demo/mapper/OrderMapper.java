package com.example.demo.mapper;

import com.example.demo.dto.OrderDTO;
import com.example.demo.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "user.id", target = "userId")
    OrderDTO toDTO(Order order);

    @Mapping(target = "user", ignore = true) // User should be set in the service layer
    @Mapping(target = "orderProducts", ignore = true) // OrderProducts should be set in the service layer
    Order toEntity(OrderDTO dto);
}