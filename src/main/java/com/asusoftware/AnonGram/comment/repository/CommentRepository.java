package com.asusoftware.AnonGram.comment.repository;

import com.asusoftware.AnonGram.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByPostId(UUID postId, Pageable pageable);
    Optional<Comment> findByIdAndUserId(UUID id, UUID userId);
    int countByPostId(UUID postId);

}