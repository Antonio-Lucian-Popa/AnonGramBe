package com.asusoftware.AnonGram.vote.repository;

import com.asusoftware.AnonGram.vote.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    boolean existsByUserIdAndPostId(UUID userId, UUID postId);
    Optional<Vote> findByUserIdAndPostId(UUID userId, UUID postId);
}