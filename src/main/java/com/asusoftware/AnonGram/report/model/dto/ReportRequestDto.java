package com.asusoftware.AnonGram.report.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ReportRequestDto {
    private UUID postId;
    private UUID userId;
    private String reason;
}
