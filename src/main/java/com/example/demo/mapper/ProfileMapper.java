package com.example.demo.mapper;

import com.example.demo.dto.ProfileDTO;
import com.example.demo.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProfileMapper {
    ProfileMapper INSTANCE = Mappers.getMapper(ProfileMapper.class);

    ProfileDTO profileToProfileDTO(Profile profile);

    Profile profileDTOToProfile(ProfileDTO profileDTO);
}