package com.asusoftware.AnonGram.vote.repository;

import com.asusoftware.AnonGram.vote.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    boolean existsByUserIdAndPostId(UUID userId, UUID postId);
    Optional<Vote> findByUserIdAndPostId(UUID userId, UUID postId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.postId = :postId AND v.voteType = 1")
    int getUpvotes(UUID postId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.postId = :postId AND v.voteType = -1")
    int getDownvotes(UUID postId);

    int countByPostIdAndVoteType(UUID postId, short voteType);



}