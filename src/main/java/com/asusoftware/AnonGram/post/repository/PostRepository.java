package com.asusoftware.AnonGram.post.repository;

import com.asusoftware.AnonGram.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findAll(Pageable pageable);
    Optional<Post> findByIdAndUserId(UUID id, UUID userId);
    Page<Post> findByTextContainingIgnoreCase(String search, Pageable pageable);

    Page<Post> findByIdInAndTextContainingIgnoreCase(List<UUID> ids, String search, Pageable pageable);
    Page<Post> findByIdIn(List<UUID> ids, Pageable pageable);

}
