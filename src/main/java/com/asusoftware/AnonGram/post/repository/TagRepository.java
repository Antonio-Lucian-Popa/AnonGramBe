package com.asusoftware.AnonGram.post.repository;

import com.asusoftware.AnonGram.post.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    Optional<Tag> findByNameIgnoreCase(String name);

    List<Tag> findByNameInIgnoreCase(List<String> names);
}


