package com.asusoftware.AnonGram.user.controller;


import com.asusoftware.AnonGram.config.KeycloakService;
import com.asusoftware.AnonGram.user.model.dto.UserResponseDto;
import com.asusoftware.AnonGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final KeycloakService keycloakService;
    private final ModelMapper mapper;

    // Get user by Keycloak ID
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@RequestParam UUID keycloakId) {
        return ResponseEntity.ok(mapper.map(userService.getByKeycloakId(keycloakId), UserResponseDto.class));
    }
    // Delete user (Keycloak + local)
    @DeleteMapping("/{keycloakId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String keycloakId) {
        keycloakService.deleteKeycloakUser(keycloakId);
        userService.deleteByKeycloakId(UUID.fromString(keycloakId));
        return ResponseEntity.noContent().build();
    }
}
