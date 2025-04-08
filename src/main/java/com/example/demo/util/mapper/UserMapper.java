package com.example.demo.util.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.entity.Profile;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName()); // Corrected from username to name

        // Map orders to OrderDTOs using OrderMapper
        if (user.getOrders() != null) {
            dto.setOrders(user.getOrders().stream()
                    .map(OrderMapper::toDto)
                    .collect(Collectors.toList()));
        }

        // Extract profile ID from Profile entity
        Profile profile = user.getProfile();
        dto.setProfileId(profile != null ? profile.getId() : null);

        return dto;
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName()); // Corrected from username to nam

        return user;
    }
}