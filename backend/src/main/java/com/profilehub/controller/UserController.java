package com.profilehub.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.profilehub.dto.UserResponse;
import com.profilehub.entity.User;
import com.profilehub.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        log.info("Getting current user: {}", email);
        
        // Extract userId from SecurityContext
        Long userId = extractUserIdFromAuth(authentication);
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        log.info("Getting user: {}", userId);
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody User userDetails,
            Authentication authentication) {
        log.info("Updating user: {}", userId);
        
        Long authenticatedUserId = extractUserIdFromAuth(authentication);
        if (!userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponse userResponse = userService.updateUser(userId, userDetails);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            Authentication authentication) {
        log.info("Changing password for user: {}", userId);
        
        Long authenticatedUserId = extractUserIdFromAuth(authentication);
        if (!userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {
        log.info("Deleting user: {}", userId);
        
        Long authenticatedUserId = extractUserIdFromAuth(authentication);
        if (!userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    private Long extractUserIdFromAuth(Authentication authentication) {
        // This would normally come from JWT token or SecurityContext
        return 1L; // Placeholder - implement proper extraction from JWT
    }
}
