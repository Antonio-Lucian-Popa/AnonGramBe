package com.asusoftware.AnonGram.post.service;

import com.asusoftware.AnonGram.comment.repository.CommentRepository;
import com.asusoftware.AnonGram.exception.PostNotFoundException;
import com.asusoftware.AnonGram.post.model.Post;
import com.asusoftware.AnonGram.post.model.PostTag;
import com.asusoftware.AnonGram.post.model.dto.PostRequestDto;
import com.asusoftware.AnonGram.post.model.dto.PostResponseDto;
import com.asusoftware.AnonGram.post.repository.PostRepository;
import com.asusoftware.AnonGram.post.repository.PostTagRepository;
import com.asusoftware.AnonGram.vote.repository.VoteRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Value("${external-link.url}")
    private String externalLinkBase;

    private final ModelMapper mapper;

    private final VoteRepository voteRepository;
    private final CommentRepository commentRepository;
    private final PostTagRepository postTagRepository;

    @Transactional
    public PostResponseDto save(PostRequestDto postdto, List<MultipartFile> images) {
        UUID postId = UUID.randomUUID();
        Post post = mapper.map(postdto, Post.class);
        post.setId(postId);
        post.setCreatedAt(LocalDateTime.now());

        // Save images
        if (images != null && !images.isEmpty()) {
            List<String> imagePaths = images.stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> saveImage(file, postId))
                    .toList();
            post.setImages(imagePaths);
        }

        // ✅ Save post and flush to ensure FK constraint will work
        Post savedPost = postRepository.save(post);

        // Save tags
        if (postdto.getTags() != null && !postdto.getTags().isEmpty()) {
            List<PostTag> tagEntities = postdto.getTags().stream()
                    .map(tag -> {
                        PostTag postTag = new PostTag();
                        postTag.setPostId(savedPost.getId());
                        postTag.setTag(tag.toLowerCase());
                        return postTag;
                    })
                    .toList();
            postTagRepository.saveAll(tagEntities);
        }

        return mapper.map(savedPost, PostResponseDto.class);
    }


    private String saveImage(MultipartFile file, UUID postId) {
        if (file.isEmpty()) throw new IllegalArgumentException("Empty file not allowed");
        try {
            String originalFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path directory = Paths.get("uploads/images", postId.toString()).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            Path destination = directory.resolve(originalFilename);

            if (!destination.getParent().equals(directory)) {
                throw new SecurityException("Invalid path detected");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return externalLinkBase + postId + "/" + originalFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public PostResponseDto findById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with ID " + id + " not found"));

        PostResponseDto dto = mapper.map(post, PostResponseDto.class);
        dto.setImages(post.getImages());
        dto.setUpvotes(voteRepository.countByPostIdAndVoteType(id, (short) 1));
        dto.setDownvotes(voteRepository.countByPostIdAndVoteType(id, (short) -1));
        dto.setCommentCount(commentRepository.countByPostId(id));

        // 🔁 Adaugă tag-urile manual din tabelul intermediar
        List<String> tags = postTagRepository.findTagsByPostId(id);
        dto.setTags(tags);

        return dto;
    }


    public Page<PostResponseDto> findAll(String search, String tags, Double radius, Pageable pageable) {
        Page<Post> posts;

        if (tags != null && !tags.isBlank()) {
            List<String> tagList = Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();

            Set<UUID> postIds = postTagRepository.findPostIdsByTags(tagList);

            if (!postIds.isEmpty()) {
                if (search != null && !search.isBlank()) {
                    posts = postRepository.findByIdInAndTextContainingIgnoreCase(postIds.stream().toList(), search, pageable);
                } else {
                    posts = postRepository.findByIdIn(postIds.stream().toList(), pageable);
                }
            } else {
                posts = Page.empty();
            }

        } else if (search != null && !search.isBlank()) {
            posts = postRepository.findByTextContainingIgnoreCase(search, pageable);
        } else {
            posts = postRepository.findAll(pageable);
        }

        return posts.map(post -> {
            PostResponseDto dto = mapper.map(post, PostResponseDto.class);
            dto.setImages(post.getImages());
            dto.setUpvotes(voteRepository.countByPostIdAndVoteType(post.getId(), (short) 1));
            dto.setDownvotes(voteRepository.countByPostIdAndVoteType(post.getId(), (short) -1));
            dto.setCommentCount(commentRepository.countByPostId(post.getId()));
            dto.setTags(postTagRepository.findTagsByPostId(post.getId()));
            return dto;
        });
    }


    public void deleteByIdIfOwned(UUID postId, UUID userId) {
        Post post = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new PostNotFoundException("Cannot delete: post does not exist or doesn't belong to user"));
        postRepository.delete(post);
    }
}