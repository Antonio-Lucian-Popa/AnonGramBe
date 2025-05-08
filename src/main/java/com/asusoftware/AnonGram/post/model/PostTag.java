package com.asusoftware.AnonGram.post.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@IdClass(PostTagId.class)
@Table(name = "post_tags")
public class PostTag {

    @Id
    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Id
    @Column(name = "tag_id", nullable = false)
    private UUID tagId;
}

