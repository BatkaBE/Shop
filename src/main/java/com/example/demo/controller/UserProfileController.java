package com.example.demo.controller;

import com.example.demo.dto.ProfileDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.service.crud.UserProfileService;
import com.example.demo.service.crud.UserService;
import com.example.demo.util.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

record CreateUserProfileRequest(
        UserDTO user,
        ProfileDTO profile
) {}

@RestController
@RequestMapping("/api")
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * 1) Create a new User together with a Profile
     */
    @PostMapping("/user‑profiles")
    public ResponseEntity<UserDTO> createUserWithProfile(
            @RequestBody CreateUserProfileRequest req
    ) {
        // map DTO → entity
        User u = UserMapper.toEntity(req.user());
        // call service
        User saved = userProfileService.createUserWithProfile(u, req.profile());
        // map back to DTO (and include profileId)
        UserDTO out = UserMapper.toDTO(saved);
        out.setProfileId(saved.getProfile().getId());
        return new ResponseEntity<>(out, HttpStatus.CREATED);
    }

    /**
     * 2) Add a Profile to an existing User
     */
    @PostMapping("/users/{userId}/profile")
    public ResponseEntity<ProfileDTO> addProfileToUser(
            @PathVariable UUID userId,
            @RequestBody ProfileDTO profileDTO
    ) {
        ProfileDTO created = userProfileService.addProfileToUser(userId, profileDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * 3) Update User and Profile together
     */
    @PutMapping("/users/{userId}/with-profile")
    public ResponseEntity<UserDTO> updateUserWithProfile(
            @PathVariable UUID userId,
            @RequestBody CreateUserProfileRequest req
    ) {
        UserDTO updated = userProfileService.updateUserWithProfile(
                userId,
                req.user(),
                req.profile()
        );
        return ResponseEntity.ok(updated);
    }

    /**
     * 4) Delete User (and their Profile if present)
     */
    @DeleteMapping("/users/{userId}/with-profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserWithProfile(@PathVariable UUID userId) {
        userProfileService.deleteUserWithProfile(userId);
    }
}
