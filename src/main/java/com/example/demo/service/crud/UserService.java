package com.example.demo.service.crud;

import com.example.demo.dto.ProfileDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.error.NotFoundError;
import com.example.demo.exception.error.ValidationError;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User saveUser(User user) {
        validateUser(user);
        return userRepository.save(user);
    }

    @Transactional
    public User createUser(UserDTO userDTO) {
        User user = UserMapper.toEntity(userDTO);
        validateUser(user);
        return userRepository.save(user);
    }

    public User findByIdOr404(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("User not found"));
    }

    public UserDTO getUserById(UUID id) {
        User user = findByIdOr404(id);
        return UserMapper.toDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User existingUser = findByIdOr404(id);
        existingUser.setName(userDTO.getName());
        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundError("User not found");
        }
        userRepository.deleteById(id);
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new ValidationError("User name cannot be empty");
        }

        // Check for duplicate username if needed
        if (userRepository.existsByName(user.getName())) {
            throw new ValidationError("Username already exists");
        }
    }

}




