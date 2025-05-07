package com.asusoftware.AnonGram.comment.controller;

import com.asusoftware.AnonGram.comment.model.dto.CommentRequestDto;
import com.asusoftware.AnonGram.comment.model.dto.CommentResponseDto;
import com.asusoftware.AnonGram.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final ModelMapper mapper;

    // ✅ Create comment
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(mapper.map(commentService.save(dto), CommentResponseDto.class));
    }

    // ✅ Get comments for a post (paginated)
    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsForPost(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<CommentResponseDto> comments = commentService.findByPostId(postId, PageRequest.of(page, size));
        return ResponseEntity.ok(comments);
    }

    // ✅ Delete comment if owned
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID id,
            @RequestParam UUID userId) {

        commentService.deleteByIdIfOwned(id, userId);
        return ResponseEntity.noContent().build();
    }
}