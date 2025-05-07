package com.asusoftware.AnonGram.comment.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CommentRequestDto {
    private UUID postId;
    private UUID userId;
    private String text;
}
