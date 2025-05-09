package com.asusoftware.AnonGram.user.service;

import com.asusoftware.AnonGram.config.KeycloakService;
import com.asusoftware.AnonGram.exception.UserNotFoundException;
import com.asusoftware.AnonGram.user.model.User;
import com.asusoftware.AnonGram.user.model.dto.LoginDto;
import com.asusoftware.AnonGram.user.model.dto.UserRegisterDto;
import com.asusoftware.AnonGram.user.model.dto.UserResponseDto;
import com.asusoftware.AnonGram.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final ModelMapper mapper;

    @Transactional
    public UserResponseDto register(UserRegisterDto dto) {
        String keycloakId = keycloakService.createKeycloakUser(dto);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(dto.getEmail());
        user.setAlias(dto.getAlias());
        user.setKeycloakId(UUID.fromString(keycloakId));
        user.setCreatedAt(LocalDateTime.now());
        user.setKeycloakId(UUID.fromString(keycloakId));
        userRepository.save(user);

        return mapper.map(user, UserResponseDto.class);
    }

    public AccessTokenResponse login(LoginDto dto) {
        return keycloakService.loginUser(dto);
    }

    public User getByKeycloakId(UUID keycloakId) {
        return userRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new UserNotFoundException("User with keycloakId " + keycloakId + " not found"));
    }

    public void deleteByKeycloakId(UUID keycloakId) {
        userRepository.findByKeycloakId(keycloakId).ifPresent(user -> {
            keycloakService.deleteKeycloakUser(keycloakId.toString()); // ← Șterge din Keycloak
            userRepository.delete(user);                               // ← Șterge din DB local
        });
    }


}