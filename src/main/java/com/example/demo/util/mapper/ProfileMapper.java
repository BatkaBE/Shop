package com.example.demo.util.mapper;
import com.example.demo.dto.ProfileDTO;
import com.example.demo.entity.Profile;

public class ProfileMapper {

    public static ProfileDTO toDTO(Profile profile) {
        if (profile == null) {
            return null;
        }

        ProfileDTO dto = new ProfileDTO();
        dto.setId(profile.getId());
        dto.setAddress(profile.getAddress());



        return dto;
    }

    public static Profile toEntity(ProfileDTO dto) {
        if (dto == null) {
            return null;
        }

        Profile profile = new Profile();
        profile.setId(dto.getId());
        profile.setAddress(dto.getAddress());

        return profile;
    }
}