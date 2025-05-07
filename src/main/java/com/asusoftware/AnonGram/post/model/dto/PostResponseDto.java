package com.asusoftware.AnonGram.post.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class PostResponseDto {
    private UUID id;
    private UUID userId;
    private String text;
    private List<String> images;
    private Double latitude;
    private Double longitude;
    private String tag;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private int upvotes;
    private int downvotes;
    private int commentCount;

}
