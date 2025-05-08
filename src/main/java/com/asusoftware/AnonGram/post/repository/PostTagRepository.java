package com.asusoftware.AnonGram.post.repository;

import com.asusoftware.AnonGram.post.model.PostTag;
import com.asusoftware.AnonGram.post.model.PostTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, PostTagId> {
    List<PostTag> findByPostId(UUID postId);
    List<PostTag> findByTagIn(List<String> tags);

    @Query("SELECT pt.postId FROM PostTag pt WHERE LOWER(pt.tag) IN :tags")
    Set<UUID> findPostIdsByTags(@Param("tags") List<String> tags);

    @Query("SELECT pt.tag FROM PostTag pt WHERE pt.postId = :postId")
    List<String> findTagsByPostId(@Param("postId") UUID postId);


}

