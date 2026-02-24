package com.profilehub.controller;

import com.profilehub.dto.UserResponse;
import com.profilehub.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCurrentUser_ReturnsUserResponse() {
        UserResponse userResponse = UserResponse.builder().id(1L).username("user").build();
        when(userService.getCurrentUser()).thenReturn(userResponse);
        ResponseEntity<UserResponse> response = userController.getCurrentUser();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user", response.getBody().getUsername());
    }
}
