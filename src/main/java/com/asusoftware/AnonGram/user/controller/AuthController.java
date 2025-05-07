package com.asusoftware.AnonGram.user.controller;

import com.asusoftware.AnonGram.config.KeycloakService;

import com.asusoftware.AnonGram.user.model.dto.LoginDto;
import com.asusoftware.AnonGram.user.model.dto.RefreshTokenDto;
import com.asusoftware.AnonGram.user.model.dto.UserRegisterDto;
import com.asusoftware.AnonGram.user.model.dto.UserResponseDto;
import com.asusoftware.AnonGram.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeycloakService keycloakService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterDto dto) {
        return ResponseEntity.ok(userService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody LoginDto dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    @PostMapping("/refresh")
    public AccessTokenResponse refresh(@RequestBody RefreshTokenDto dto) {
       // return keycloakService.refreshToken(dto.getRefreshToken());
        return null;
    }
}
