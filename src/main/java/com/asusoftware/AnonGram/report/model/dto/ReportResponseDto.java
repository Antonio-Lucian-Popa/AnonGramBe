package com.asusoftware.AnonGram.report.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ReportResponseDto {
    private UUID id;
    private UUID postId;
    private UUID userId;
    private String reason;
    private java.time.LocalDateTime createdAt;
}