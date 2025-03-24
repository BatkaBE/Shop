package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "profile.address", target = "profileDTO.address")
    UserDTO userToUserDTO(User user);

    @Mapping(source = "profileDTO.address", target = "profile.address")
    User userDTOToUser(UserDTO userDTO);
}