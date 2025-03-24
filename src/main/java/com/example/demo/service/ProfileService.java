package com.example.demo.service;

import com.example.demo.dto.ProfileDTO;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    // Create
    public ProfileDTO createProfile(ProfileDTO profileDTO) {
        Profile profile = convertToEntity(profileDTO);
        Profile savedProfile = profileRepository.save(profile);
        return convertToDTO(savedProfile);
    }

    // Read
    public ProfileDTO getProfileById(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return convertToDTO(profile);
    }

    public List<ProfileDTO> getAllProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Update
    public ProfileDTO updateProfile(Long id, ProfileDTO profileDTO) {
        Profile existingProfile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        existingProfile.setAddress(profileDTO.getAddress());

        if (!existingProfile.getUser().getId().equals(profileDTO.getUserId())) {
            User newUser = userRepository.findById(profileDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            existingProfile.setUser(newUser);
        }

        Profile updatedProfile = profileRepository.save(existingProfile);
        return convertToDTO(updatedProfile);
    }

    // Delete
    public void deleteProfile(Long id) {
        if (!profileRepository.existsById(id)) {
            throw new RuntimeException("Profile not found");
        }
        profileRepository.deleteById(id);
    }

    private ProfileDTO convertToDTO(Profile profile) {
        return new ProfileDTO(
                profile.getId(),
                profile.getAddress(),
                profile.getUser().getId()
        );
    }

    private Profile convertToEntity(ProfileDTO profileDTO) {
        Profile profile = new Profile();
        profile.setId(profileDTO.getId());
        profile.setAddress(profileDTO.getAddress());

        User user = userRepository.findById(profileDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        profile.setUser(user);

        return profile;
    }
}