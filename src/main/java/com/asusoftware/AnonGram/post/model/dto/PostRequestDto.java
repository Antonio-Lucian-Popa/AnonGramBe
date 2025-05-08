package com.asusoftware.AnonGram.post.model.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PostRequestDto {
    private UUID userId;
    private String text;
    private Double latitude;
    private Double longitude;
    private List<String> tags;
    private String expiresAt; // sau `LocalDateTime` dacă parsezi direct
}
