package com.example.demo.service.crud;

import com.example.demo.dto.ProfileDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.exception.error.NotFoundError;
import com.example.demo.exception.error.ValidationError;
import com.example.demo.util.mapper.ProfileMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserProfileService {
    private final UserService userService;
    private final ProfileService profileService;

    public UserProfileService(UserService userService,
                              ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @Transactional(rollbackOn = Exception.class)
    public User createUserWithProfile(User user, ProfileDTO profileDTO) {
        User savedUser = userService.saveUser(user);
        Profile profile = new Profile();
        profile.setAddress(profileDTO.getAddress());
        profile.setUser(savedUser);
        profileService.createProfile(profile);
        return savedUser;
    }

    @Transactional(rollbackOn = Exception.class)
    public ProfileDTO addProfileToUser(UUID userId, ProfileDTO profileDTO) {
        // This will check if user exists
        User user = userService.findByIdOr404(userId);

        // Check if profile already exists
        if (user.getProfile() != null) {
            throw new ValidationError("User already has a profile");
        }

        // Create and save profile
        Profile profile = new Profile();
        profile.setAddress(profileDTO.getAddress());
        profile.setUser(user);
        Profile savedProfile = profileService.saveOrFail(profile);

        return ProfileMapper.toDTO(savedProfile);
    }

    @Transactional(rollbackOn = Exception.class)
    public UserDTO updateUserWithProfile(UUID userId, UserDTO userDTO, ProfileDTO profileDTO) {
        // Update user
        UserDTO updatedUser = userService.updateUser(userId, userDTO);

        // Update or create profile
        if (updatedUser.getProfileId() != null) {
            // Update existing profile
            profileService.updateProfile(updatedUser.getProfileId(), profileDTO);
        } else {
            // Create new profile
            ProfileDTO newProfile = this.addProfileToUser(userId, profileDTO);
            updatedUser.setProfileId(newProfile.getId());
        }

        return updatedUser;
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteUserWithProfile(UUID userId) {
        User user = userService.findByIdOr404(userId);

        // Delete profile if exists
        if (user.getProfile() != null) {
            profileService.deleteProfile(user.getProfile().getId());
        }

        // Delete user
        userService.deleteUser(userId);
    }

}
