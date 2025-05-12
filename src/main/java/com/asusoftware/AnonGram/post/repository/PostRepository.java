package com.asusoftware.AnonGram.post.repository;

import com.asusoftware.AnonGram.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query(
            value = """
        SELECT * FROM posts p
        WHERE (:search IS NULL OR p.text ILIKE CONCAT('%', :search, '%'))
          AND (:tagCount = 0 OR EXISTS (
              SELECT 1 FROM post_tags pt
              WHERE pt.post_id = p.id AND pt.tag_id = ANY(:tagIds)
          ))
         AND (
                          :radius IS NULL OR (
                            (
                              p.latitude IS NOT NULL AND p.longitude IS NOT NULL AND
                              6371 * acos(
                                cos(radians(:latitude)) * cos(radians(p.latitude)) *
                                cos(radians(p.longitude) - radians(:longitude)) +
                                sin(radians(:latitude)) * sin(radians(p.latitude))
                              ) <= :radius
                            )
                            OR (p.latitude IS NULL OR p.longitude IS NULL) -- adăugat
                          )
                        )
        """,
            countQuery = """
        SELECT COUNT(*) FROM posts p
        WHERE (:search IS NULL OR p.text ILIKE CONCAT('%', :search, '%'))
          AND (:tagCount = 0 OR EXISTS (
              SELECT 1 FROM post_tags pt
              WHERE pt.post_id = p.id AND pt.tag_id = ANY(:tagIds)
          ))
          AND (:radius IS NULL OR (
              p.latitude IS NOT NULL AND p.longitude IS NOT NULL AND
              6371 * acos(
                  cos(radians(:latitude)) * cos(radians(p.latitude)) *
                  cos(radians(p.longitude) - radians(:longitude)) +
                  sin(radians(:latitude)) * sin(radians(p.latitude))
              ) <= :radius
          ))
        """,
            nativeQuery = true
    )
    Page<Post> findFilteredPostsNative(
            @Param("search") String search,
            @Param("tagIds") UUID[] tagIds,
            @Param("tagCount") int tagCount,
            @Param("radius") Double radius,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            Pageable pageable
    );

    int deleteByExpiresAtBefore(LocalDateTime now);


    @Query(value = """
    SELECT * FROM posts p
    WHERE p.user_id = :userId
    ORDER BY p.created_at DESC
    """,
            countQuery = """
    SELECT COUNT(*) FROM posts p
    WHERE p.user_id = :userId
    """,
            nativeQuery = true)
    Page<Post> findByUserId(@Param("userId") UUID userId, Pageable pageable);


}


