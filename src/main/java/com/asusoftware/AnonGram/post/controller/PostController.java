package com.asusoftware.AnonGram.post.controller;

import com.asusoftware.AnonGram.post.model.Post;
import com.asusoftware.AnonGram.post.model.dto.PostRequestDto;
import com.asusoftware.AnonGram.post.model.dto.PostResponseDto;
import com.asusoftware.AnonGram.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ModelMapper mapper;


    // ✅ Create a new post with optional images
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<PostResponseDto> createPost(
            @RequestPart("post") PostRequestDto postDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        System.out.println("Received post: " + postDto);
        return ResponseEntity.ok(postService.save(postDto, images));
    }

    // ✅ Get all posts (paginated)
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius,
            @AuthenticationPrincipal Jwt jwt) {

        Page<PostResponseDto> posts = postService.findAll(search, tags, latitude, longitude, radius, PageRequest.of(page, size), UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user-profile/posts")
    public Page<PostResponseDto> getPostsByUserId(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postService.findAllByUserId(UUID.fromString(jwt.getSubject()), pageable);
    }



    // ✅ Get post by ID
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        PostResponseDto post = postService.findById(id, UUID.fromString(jwt.getSubject()));
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
