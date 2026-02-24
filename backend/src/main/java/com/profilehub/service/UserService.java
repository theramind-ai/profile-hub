package com.profilehub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.profilehub.dto.UserResponse;
import com.profilehub.entity.User;
import com.profilehub.exception.ApiException;
import com.profilehub.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", 404));
        return mapUserToResponse(user);
    }

    public UserResponse updateUser(Long userId, User userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", 404));

        if (userDetails.getFirstName() != null) {
            user.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            user.setLastName(userDetails.getLastName());
        }
        if (userDetails.getBio() != null) {
            user.setBio(userDetails.getBio());
        }
        if (userDetails.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(userDetails.getProfilePictureUrl());
        }

        user = userRepository.save(user);
        log.info("User updated: {}", userId);

        return mapUserToResponse(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", 404));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ApiException("Old password is incorrect", 400);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", userId);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", 404));
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deleted: {}", userId);
    }

    private UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .bio(user.getBio())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
