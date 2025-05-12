package com.asusoftware.AnonGram.comment.service;

import com.asusoftware.AnonGram.comment.model.Comment;
import com.asusoftware.AnonGram.comment.model.dto.CommentRequestDto;
import com.asusoftware.AnonGram.comment.model.dto.CommentResponseDto;
import com.asusoftware.AnonGram.comment.repository.CommentRepository;
import com.asusoftware.AnonGram.exception.CommentNotFoundException;
import com.asusoftware.AnonGram.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final ModelMapper mapper;

    public CommentResponseDto save(CommentRequestDto dto) {
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID()); // Generăm ID-ul
        comment.setPostId(dto.getPostId());
        comment.setUserId(dto.getUserId());
        comment.setText(dto.getText());
        comment.setCreatedAt(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        CommentResponseDto commentResponseDto = mapper.map(saved, CommentResponseDto.class);
        commentResponseDto.setUserAlias(postService.getUserAlias(comment.getUserId()));
        return commentResponseDto;
    }


    public Page<CommentResponseDto> findByPostId(UUID postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable).map(comment -> {
            CommentResponseDto dto = mapper.map(comment, CommentResponseDto.class);
            dto.setUserAlias(postService.getUserAlias(comment.getUserId()));
            return dto;
        });
    }

    public void deleteByIdIfOwned(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findByIdAndUserId(commentId, userId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found or not owned by user"));
        commentRepository.delete(comment);
    }
}