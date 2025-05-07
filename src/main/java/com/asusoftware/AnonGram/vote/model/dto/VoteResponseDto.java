package com.asusoftware.AnonGram.vote.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VoteResponseDto {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private short voteType;
    private LocalDateTime createdAt;
}
