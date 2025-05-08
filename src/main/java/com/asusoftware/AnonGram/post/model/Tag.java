package com.asusoftware.AnonGram.post.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;
}

