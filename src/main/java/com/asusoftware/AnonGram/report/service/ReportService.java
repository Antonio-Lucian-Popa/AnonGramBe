package com.asusoftware.AnonGram.report.service;

import com.asusoftware.AnonGram.report.model.Report;
import com.asusoftware.AnonGram.report.model.dto.ReportRequestDto;
import com.asusoftware.AnonGram.report.model.dto.ReportResponseDto;
import com.asusoftware.AnonGram.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ModelMapper mapper;

    public ReportResponseDto save(ReportRequestDto dto) {
        Report report = new Report();
        report.setId(UUID.randomUUID());
        report.setPostId(dto.getPostId());
        report.setUserId(dto.getUserId());
        report.setReason(dto.getReason());
        report.setCreatedAt(LocalDateTime.now());

        Report saved = reportRepository.save(report);
        return mapper.map(saved, ReportResponseDto.class);
    }


    public Page<ReportResponseDto> findAll(Pageable pageable) {
        return reportRepository.findAll(pageable).map(report -> mapper.map(report, ReportResponseDto.class));
    }
}