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

    boolean existsById(PostTagId id);

    List<PostTag> findByPostId(UUID postId);

    @Query("""
        SELECT pt.postId 
        FROM PostTag pt 
        JOIN Tag t ON pt.tagId = t.id 
        WHERE LOWER(t.name) IN :tagNames
    """)
    Set<UUID> findPostIdsByTagNames(@Param("tagNames") List<String> tagNames);

    @Query("""
        SELECT t.name 
        FROM PostTag pt 
        JOIN Tag t ON pt.tagId = t.id 
        WHERE pt.postId = :postId
    """)
    List<String> findTagNamesByPostId(@Param("postId") UUID postId);

    @Query("SELECT DISTINCT pt.tagId FROM PostTag pt JOIN Tag t ON pt.tagId = t.id WHERE LOWER(t.name) IN :tagNames")
    List<UUID> findTagIdsByTagNames(@Param("tagNames") List<String> tagNames);

}




