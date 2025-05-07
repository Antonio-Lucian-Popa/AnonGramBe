package com.asusoftware.AnonGram.post.controller;

import com.asusoftware.AnonGram.post.model.Post;
import com.asusoftware.AnonGram.post.model.dto.PostRequestDto;
import com.asusoftware.AnonGram.post.model.dto.PostResponseDto;
import com.asusoftware.AnonGram.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ModelMapper mapper;


    // ✅ Create a new post with optional images
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PostResponseDto> createPost(
            @RequestPart("post") PostRequestDto postDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(postService.save(postDto, images));
    }

    // ✅ Get all posts (paginated)
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostResponseDto> posts = postService.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(posts);
    }

    // ✅ Get post by ID
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable UUID id) {
        PostResponseDto post = postService.findById(id);
        return ResponseEntity.ok(post);
    }

    // ✅ Delete post if owner
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID id,
            @RequestParam("userId") UUID userId) {

        postService.deleteByIdIfOwned(id, userId);
        return ResponseEntity.noContent().build();
    }
}
