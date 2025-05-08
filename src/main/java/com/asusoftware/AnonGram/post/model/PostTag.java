package com.asusoftware.AnonGram.post.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "post_tags")
@IdClass(PostTagId.class)
public class PostTag {
    @Id
    @Column(name = "post_id")
    private UUID postId;

    @Id
    @Column(name = "tag")
    private String tag;
}
