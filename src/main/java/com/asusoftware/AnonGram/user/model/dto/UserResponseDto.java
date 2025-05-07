package com.asusoftware.AnonGram.user.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserResponseDto {
    private UUID id;
    private String email;
    private String alias;
    private Instant createdAt;
}
