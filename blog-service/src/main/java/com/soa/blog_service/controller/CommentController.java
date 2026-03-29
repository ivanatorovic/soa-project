package com.soa.blog_service.controller;

import com.soa.blog_service.dto.CreateCommentRequest;
import com.soa.blog_service.dto.UpdateCommentRequest;
import com.soa.blog_service.security.JwtUserPrincipal;
import com.soa.blog_service.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return commentService.createComment(request, principal.getUserId(), principal.getUsername());
    }

    @GetMapping("/blog/{blogId}")
    public ResponseEntity<?> getCommentsByBlogId(@PathVariable Long blogId) {
        return commentService.getCommentsByBlogId(blogId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long id,
            @RequestBody UpdateCommentRequest request,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return commentService.updateComment(id, request.getText(), principal.getUserId());
    }
}