package com.profilehub.service;

import com.profilehub.dto.UserResponse;
import com.profilehub.entity.User;
import com.profilehub.exception.ApiException;
import com.profilehub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_UserExists_ReturnsUserResponse() {
        User user = User.builder().id(1L).email("a@b.com").username("user").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserResponse response = userService.getUserById(1L);
        assertNotNull(response);
        assertEquals("user", response.getUsername());
    }

    @Test
    void getUserById_UserNotFound_ThrowsApiException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        ApiException ex = assertThrows(ApiException.class, () -> userService.getUserById(99L));
        assertEquals(404, ex.getStatusCode());
    }
}
