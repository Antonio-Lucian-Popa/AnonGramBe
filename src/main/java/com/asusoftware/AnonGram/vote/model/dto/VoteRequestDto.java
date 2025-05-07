package com.asusoftware.AnonGram.vote.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class VoteRequestDto {
    private UUID postId;
    private UUID userId;
    private short voteType; // 1 or -1
}
