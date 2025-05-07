package com.asusoftware.AnonGram.comment.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CommentResponseDto {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private String text;
    private LocalDateTime createdAt;
}
