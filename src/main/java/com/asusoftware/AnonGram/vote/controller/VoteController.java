package com.asusoftware.AnonGram.vote.controller;

import com.asusoftware.AnonGram.vote.model.dto.VoteRequestDto;
import com.asusoftware.AnonGram.vote.model.dto.VoteResponseDto;
import com.asusoftware.AnonGram.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<VoteResponseDto> vote(@RequestBody VoteRequestDto dto) {
        return ResponseEntity.ok(voteService.save(dto));
    }

    @PutMapping
    public ResponseEntity<VoteResponseDto> toggleVote(@RequestBody VoteRequestDto dto) {
        return ResponseEntity.ok(voteService.toggleVote(dto));
    }

    @GetMapping("/hasVoted/{userId}/{postId}")
    public ResponseEntity<Boolean> hasUserVoted(@RequestParam(name = "userId") UUID userId, @RequestParam(name = "postId") UUID postId) {
        return ResponseEntity.ok(voteService.hasUserVoted(userId, postId));
    }
}
