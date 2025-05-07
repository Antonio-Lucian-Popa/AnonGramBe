package com.asusoftware.AnonGram.post.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PostRequestDto {
    private UUID userId;
    private String text;
    private Double latitude;
    private Double longitude;
    private String tag;
    private String expiresAt; // sau `LocalDateTime` dacă parsezi direct
}
