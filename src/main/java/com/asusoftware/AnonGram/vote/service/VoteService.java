package com.asusoftware.AnonGram.vote.service;

import com.asusoftware.AnonGram.exception.VoteNotAllowedException;
import com.asusoftware.AnonGram.vote.model.Vote;
import com.asusoftware.AnonGram.vote.model.dto.VoteRequestDto;
import com.asusoftware.AnonGram.vote.model.dto.VoteResponseDto;
import com.asusoftware.AnonGram.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final ModelMapper mapper;

    public VoteResponseDto save(VoteRequestDto dto) {
        boolean alreadyVoted = voteRepository.existsByUserIdAndPostId(dto.getUserId(), dto.getPostId());
        if (alreadyVoted) {
            throw new VoteNotAllowedException("You have already voted on this post.");
        }

        Vote vote = new Vote();
        vote.setId(UUID.randomUUID());
        vote.setVoteType(dto.getVoteType());
        vote.setUserId(dto.getUserId());
        vote.setPostId(dto.getPostId());
        vote.setCreatedAt(LocalDateTime.now());

        return mapper.map(voteRepository.save(vote), VoteResponseDto.class);
    }

    public boolean hasUserVoted(UUID userId, UUID postId) {
        return voteRepository.existsByUserIdAndPostId(userId, postId);
    }
}
