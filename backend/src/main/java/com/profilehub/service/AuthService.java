package com.profilehub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.profilehub.dto.AuthResponse;
import com.profilehub.dto.LoginRequest;
import com.profilehub.dto.RegisterRequest;
import com.profilehub.dto.UserResponse;
import com.profilehub.entity.Role;
import com.profilehub.entity.User;
import com.profilehub.exception.ApiException;
import com.profilehub.repository.UserRepository;
import com.profilehub.security.JwtTokenProvider;

@Slf4j
@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            String token = tokenProvider.generateToken(authentication);
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new ApiException("User not found", 404));

            UserResponse userResponse = mapUserToResponse(user);

            return AuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .expiresIn(tokenProvider.getExpirationTime())
                    .user(userResponse)
                    .build();

        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            throw new ApiException("Invalid email or password", 401);
        }
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ApiException("Email already registered", 400);
        }

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ApiException("Username already taken", 400);
        }

        // Create new user
        User user = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .role(Role.USER)
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        String token = tokenProvider.generateTokenFromId(user.getId());
        UserResponse userResponse = mapUserToResponse(user);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationTime())
                .user(userResponse)
                .build();
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
