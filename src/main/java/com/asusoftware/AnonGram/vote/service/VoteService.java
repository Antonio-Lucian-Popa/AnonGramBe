package com.asusoftware.AnonGram.vote.service;

import com.asusoftware.AnonGram.vote.model.Vote;
import com.asusoftware.AnonGram.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;

    public Vote save(Vote vote) {
        vote.setCreatedAt(LocalDateTime.now());
        return voteRepository.save(vote);
    }

    public boolean hasUserVoted(UUID userId, UUID postId) {
        return voteRepository.existsByUserIdAndPostId(userId, postId);
    }
}
