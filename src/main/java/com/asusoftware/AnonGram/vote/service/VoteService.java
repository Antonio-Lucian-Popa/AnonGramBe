package com.asusoftware.AnonGram.vote.service;

import com.asusoftware.AnonGram.exception.VoteNotAllowedException;
import com.asusoftware.AnonGram.vote.model.Vote;
import com.asusoftware.AnonGram.vote.model.dto.VoteRequestDto;
import com.asusoftware.AnonGram.vote.model.dto.VoteResponseDto;
import com.asusoftware.AnonGram.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final ModelMapper mapper;

    public VoteResponseDto save(VoteRequestDto dto) {
        return toggleVote(dto);
    }

    @Transactional
    public VoteResponseDto toggleVote(VoteRequestDto dto) {
        Optional<Vote> existingVote = voteRepository.findByUserIdAndPostId(dto.getUserId(), dto.getPostId());

        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            if (vote.getVoteType() == dto.getVoteType()) {
                // Same voteType => remove vote
                voteRepository.delete(vote);
                return null; // or return a DTO with info that vote was removed
            } else {
                // Different voteType => update it
                vote.setVoteType(dto.getVoteType());
                vote.setCreatedAt(LocalDateTime.now());
                return mapper.map(voteRepository.save(vote), VoteResponseDto.class);
            }
        }

        // No vote yet => create new
        Vote newVote = new Vote();
        newVote.setId(UUID.randomUUID());
        newVote.setUserId(dto.getUserId());
        newVote.setPostId(dto.getPostId());
        newVote.setVoteType(dto.getVoteType());
        newVote.setCreatedAt(LocalDateTime.now());

        return mapper.map(voteRepository.save(newVote), VoteResponseDto.class);
    }


    public boolean hasUserVoted(UUID userId, UUID postId) {
        return voteRepository.existsByUserIdAndPostId(userId, postId);
    }
}
