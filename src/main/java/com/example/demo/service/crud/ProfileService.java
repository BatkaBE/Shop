package com.example.demo.service.crud;

import com.example.demo.dto.ProfileDTO;
import com.example.demo.entity.Profile;
import com.example.demo.repository.ProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;


    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional
    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    public Profile saveOrFail(Profile profile) {
        return profileRepository.save(profile);
    }

    public void updateProfile(UUID profileId, ProfileDTO profileDTO) {
    }

    public void deleteProfile(UUID id) {
    }
}