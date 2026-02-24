package com.profilehub.controller;

import com.profilehub.dto.AuthResponse;
import com.profilehub.dto.LoginRequest;
import com.profilehub.dto.RegisterRequest;
import com.profilehub.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_ReturnsOkWithAuthResponse() {
        LoginRequest loginRequest = new LoginRequest();
        AuthResponse authResponse = AuthResponse.builder().token("jwt").build();
        when(authService.login(loginRequest)).thenReturn(authResponse);
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt", response.getBody().getToken());
    }

    @Test
    void register_ReturnsCreatedWithAuthResponse() {
        RegisterRequest registerRequest = new RegisterRequest();
        AuthResponse authResponse = AuthResponse.builder().token("jwt").build();
        when(authService.register(registerRequest)).thenReturn(authResponse);
        ResponseEntity<AuthResponse> response = authController.register(registerRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("jwt", response.getBody().getToken());
    }
}
