package com.profilehub.service;

import com.profilehub.dto.AuthResponse;
import com.profilehub.dto.LoginRequest;
import com.profilehub.dto.UserResponse;
import com.profilehub.entity.User;
import com.profilehub.exception.ApiException;
import com.profilehub.repository.UserRepository;
import com.profilehub.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Success_ReturnsAuthResponse() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mocked-jwt-token");

        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getUser());
        assertEquals("testuser", response.getUser().getUsername());
    }

    @Test
    void login_InvalidCredentials_ThrowsApiException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("wrong@example.com");
        loginRequest.setPassword("wrongpass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        // Act & Assert
        ApiException ex = assertThrows(ApiException.class, () -> authService.login(loginRequest));
        assertEquals(401, ex.getStatusCode());
        assertEquals("Invalid email or password", ex.getMessage());
    }
}
