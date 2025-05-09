package com.asusoftware.AnonGram.post.service;

import com.asusoftware.AnonGram.comment.repository.CommentRepository;
import com.asusoftware.AnonGram.exception.PostNotFoundException;
import com.asusoftware.AnonGram.post.model.Post;
import com.asusoftware.AnonGram.post.model.PostTag;
import com.asusoftware.AnonGram.post.model.PostTagId;
import com.asusoftware.AnonGram.post.model.Tag;
import com.asusoftware.AnonGram.post.model.dto.PostRequestDto;
import com.asusoftware.AnonGram.post.model.dto.PostResponseDto;
import com.asusoftware.AnonGram.post.repository.PostRepository;
import com.asusoftware.AnonGram.post.repository.PostTagRepository;
import com.asusoftware.AnonGram.post.repository.TagRepository;
import com.asusoftware.AnonGram.user.model.User;
import com.asusoftware.AnonGram.user.repository.UserRepository;
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
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponseDto save(PostRequestDto postDto, List<MultipartFile> images) {
        UUID postId = UUID.randomUUID();
        Post post = mapper.map(postDto, Post.class);
        post.setId(postId);
        post.setCreatedAt(LocalDateTime.now());

        if (images != null && !images.isEmpty()) {
            List<String> imagePaths = images.stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> saveImage(file, postId))
                    .toList();
            post.setImages(imagePaths);
        }

        Post savedPost = postRepository.save(post);

        if (postDto.getTags() != null && !postDto.getTags().isEmpty()) {
            Set<String> seenTags = new HashSet<>();

            for (String tagName : postDto.getTags()) {
                String normalized = tagName.trim().toLowerCase();

                if (seenTags.contains(normalized)) continue;
                seenTags.add(normalized);

                Tag tag = tagRepository.findByNameIgnoreCase(normalized)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setId(UUID.randomUUID());
                            newTag.setName(normalized);
                            return tagRepository.save(newTag);
                        });

                PostTagId tagId = new PostTagId(savedPost.getId(), tag.getId());
                if (!postTagRepository.existsById(tagId)) {
                    PostTag postTag = new PostTag();
                    postTag.setPostId(savedPost.getId());
                    postTag.setTagId(tag.getId());
                    postTagRepository.save(postTag);
                }
            }
        }

        // ✅ Include și tag-urile în răspunsul DTO
        PostResponseDto response = mapper.map(savedPost, PostResponseDto.class);
        response.setImages(savedPost.getImages());
        response.setTags(postTagRepository.findTagNamesByPostId(savedPost.getId()));
        response.setUpvotes(0);  // implicit nou
        response.setDownvotes(0);
        response.setCommentCount(0);

        return response;
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

    public PostResponseDto findById(UUID id, UUID jwtUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with ID " + id + " not found"));

        User user = userRepository.findByKeycloakId(jwtUserId)
                .orElseThrow(() -> new NoSuchElementException("Owner with ID " + jwtUserId + " not found"));
        PostResponseDto dto = mapper.map(post, PostResponseDto.class);
        dto.setImages(post.getImages());
        dto.setUpvotes(voteRepository.countByPostIdAndVoteType(id, (short) 1));
        dto.setDownvotes(voteRepository.countByPostIdAndVoteType(id, (short) -1));
        dto.setCommentCount(commentRepository.countByPostId(id));
        dto.setTags(postTagRepository.findTagNamesByPostId(id));
        dto.setUserAlias(getUserAlias(post.getUserId()));

        // Verifică dacă user-ul logat a votat această postare
        voteRepository.findByPostIdAndUserId(id, user.getId())
                .ifPresent(v -> dto.setCurrentUserVote(v.getVoteType()));

        return dto;
    }


    public Page<PostResponseDto> findAll(
            String search,
            String tags,
            Double latitude,
            Double longitude,
            Double radius,
            Pageable pageable,
            UUID jwtUserId
    ) {
        // Validare user (dacă jwtUserId e transmis)
        final User user = jwtUserId != null
                ? userRepository.findByKeycloakId(jwtUserId)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + jwtUserId + " not found"))
                : null;


        // Preluare tag-uri dacă sunt
        List<UUID> tagIds = null;
        if (tags != null && !tags.isBlank()) {
            List<String> tagList = Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .toList();
            tagIds = postTagRepository.findTagIdsByTagNames(tagList);
            if (tagIds.isEmpty()) return Page.empty(); // Fără rezultate
        }

        UUID[] tagArray = tagIds != null ? tagIds.toArray(new UUID[0]) : new UUID[0];
        int tagCount = tagArray.length;

        // Căutare postări
        Page<Post> posts = postRepository.findFilteredPostsNative(
                search,
                tagArray,
                tagCount,
                radius,
                latitude,
                longitude,
                pageable
        );

        return posts.map(post -> {
            PostResponseDto dto = mapper.map(post, PostResponseDto.class);
            dto.setImages(post.getImages());
            dto.setUpvotes(voteRepository.countByPostIdAndVoteType(post.getId(), (short) 1));
            dto.setDownvotes(voteRepository.countByPostIdAndVoteType(post.getId(), (short) -1));
            dto.setCommentCount(commentRepository.countByPostId(post.getId()));
            dto.setTags(postTagRepository.findTagNamesByPostId(post.getId()));
            dto.setUserAlias(getUserAlias(post.getUserId()));

            // Setare vot curent (dacă e logat)
            if (user != null) {
                voteRepository.findByPostIdAndUserId(post.getId(), user.getId())
                        .ifPresent(v -> dto.setCurrentUserVote(v.getVoteType()));
            }

            return dto;
        });
    }


    private String getUserAlias(UUID userId) {
        return userRepository.findAliasById(userId)
                .orElse("Anonymous");
    }


    public void deleteByIdIfOwned(UUID postId, UUID userId) {
        Post post = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new PostNotFoundException("Cannot delete: post does not exist or doesn't belong to user"));
        postRepository.delete(post);
    }
}