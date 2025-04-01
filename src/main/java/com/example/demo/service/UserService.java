package com.example.demo.service;

import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.ProfileDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.Profile;
import com.example.demo.entity.User;
import com.example.demo.exception.NotFoundError;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    public UserService(UserRepository userRepository,
                       ProfileRepository profileRepository,
                       ProfileService profileService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.profileService = profileService;
    }

    @Transactional
    public User createUserWithProfile(User user, ProfileDTO profileDTO) {
        User savedUser = userRepository.save(user);
        Profile profile = new Profile();
        profile.setAddress(profileDTO.getAddress());
        profile.setUser(savedUser);
        profileService.createProfile(profile);  // Use injected service
        return savedUser;
    }


    @Transactional(readOnly = true)
    public ProfileDTO getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError("Хэрэглэгч олдсонгүй"));

        if(user.getProfile() == null) {
            throw new NotFoundError("Профайл олдсонгүй");
        }

        return convertToDTO(user.getProfile());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Хэрэглэгч олдсонгүй"));
        return convertToDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Хэрэглэгч олдсонгүй"));

        existingUser.setName(userDTO.getName());
        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundError("Хэрэглэгч олдсонгүй");
        }
        userRepository.deleteById(id);
    }

    public UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getOrders().stream()
                        .map(this::convertOrderToDTO) // Ensure mapping is correct
                        .collect(Collectors.toList()),
                user.getProfile() != null ? user.getProfile().getId() : null
        );
    }
    private OrderDTO convertOrderToDTO(Order order) {
        List<Long> productIds = order.getOrderProducts().stream()
                .map(op -> op.getProduct().getId())
                .collect(Collectors.toList());

        return new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getUser().getId(),
                productIds
        );
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        return user;
    }

    private ProfileDTO convertToDTO(Profile profile) {
        return new ProfileDTO(
                profile.getId(),
                profile.getAddress()
        );
    }

}