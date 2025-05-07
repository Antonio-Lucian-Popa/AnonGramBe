package com.asusoftware.AnonGram.report.controller;

import com.asusoftware.AnonGram.report.model.dto.ReportRequestDto;
import com.asusoftware.AnonGram.report.model.dto.ReportResponseDto;
import com.asusoftware.AnonGram.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponseDto> createReport(@RequestBody ReportRequestDto dto) {
        return ResponseEntity.ok(reportService.save(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ReportResponseDto>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(reportService.findAll(PageRequest.of(page, size)));
    }
}
