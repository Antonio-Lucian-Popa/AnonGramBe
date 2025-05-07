package com.asusoftware.AnonGram.post.service;

import com.asusoftware.AnonGram.exception.PostNotFoundException;
import com.asusoftware.AnonGram.post.model.Post;
import com.asusoftware.AnonGram.post.model.dto.PostRequestDto;
import com.asusoftware.AnonGram.post.model.dto.PostResponseDto;
import com.asusoftware.AnonGram.post.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Value("${external-link.url}")
    private final String externalLinkBase;

    private final ModelMapper mapper;

    public PostResponseDto save(PostRequestDto postdto, List<MultipartFile> images) {
        UUID postId = UUID.randomUUID();
        Post post = mapper.map(postdto, Post.class);
        post.setId(postId);
        post.setCreatedAt(LocalDateTime.now());
        // Save images and collect their URLs
        if (images != null && !images.isEmpty()) {
            List<String> imagePaths = images.stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> saveImage(file, postId))
                    .toList();
            post.setImages(imagePaths);
        }

        return mapper.map(postRepository.save(post), PostResponseDto.class);
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
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("Post with ID " + id + " not found"));
        return mapper.map(post, PostResponseDto.class);
    }

    public Page<PostResponseDto> findAll(Pageable pageable) {
        return postRepository.findAll(pageable).map(post -> {
            PostResponseDto postResponseDto = mapper.map(post, PostResponseDto.class);
            postResponseDto.setImages(post.getImages());
            return postResponseDto;
        });
    }

    public void deleteByIdIfOwned(UUID postId, UUID userId) {
        Post post = postRepository.findByIdAndUserId(postId, userId)
                .orElseThrow(() -> new PostNotFoundException("Cannot delete: post does not exist or doesn't belong to user"));
        postRepository.delete(post);
    }
}