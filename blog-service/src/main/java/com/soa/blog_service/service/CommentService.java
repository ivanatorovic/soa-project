package com.soa.blog_service.service;

import com.soa.blog_service.dto.CommentResponse;
import com.soa.blog_service.dto.CreateCommentRequest;
import com.soa.blog_service.model.Blog;
import com.soa.blog_service.model.Comment;
import com.soa.blog_service.repository.BlogRepository;
import com.soa.blog_service.repository.CommentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;

    public CommentService(CommentRepository commentRepository, BlogRepository blogRepository) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
    }

    public ResponseEntity<?> createComment(CreateCommentRequest request, Long authorId, String authorUsername) {
        Blog blog = blogRepository.findById(request.getBlogId()).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Ne postoji blog sa ID: " + request.getBlogId()));
        }

        if (request.getText() == null || request.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tekst komentara ne sme biti prazan"));
        }

        Comment comment = new Comment();
        comment.setBlog(blog);
        comment.setAuthorId(authorId);
        comment.setAuthorUsername(authorUsername);
        comment.setText(request.getText());

        Comment savedComment = commentRepository.save(comment);

        CommentResponse response = new CommentResponse(
                savedComment.getId(),
                savedComment.getBlog().getId(),
                savedComment.getAuthorUsername(),
                savedComment.getText(),
                savedComment.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getCommentsByBlogId(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Ne postoji blog sa ID: " + blogId));
        }

        List<Comment> comments = commentRepository.findByBlogIdOrderByCreatedAtDesc(blogId);
        if (comments.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Nema komentara za ovaj blog"));
        }

        List<CommentResponse> response = comments.stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getBlog().getId(),
                        comment.getAuthorUsername(),
                        comment.getText(),
                        comment.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateComment(Long id, String newText, Long currentUserId) {
        Comment comment = commentRepository.findById(id).orElse(null);

        if (comment == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Ne postoji komentar sa ID: " + id));
        }

        if (!comment.getAuthorId().equals(currentUserId)) {
            return ResponseEntity.status(403).body(Map.of("message", "Mozete menjati samo svoj komentar"));
        }

        if (newText == null || newText.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Tekst komentara ne sme biti prazan"));
        }

        comment.setText(newText);
        Comment updatedComment = commentRepository.save(comment);

        CommentResponse response = new CommentResponse(
                updatedComment.getId(),
                updatedComment.getBlog().getId(),
                updatedComment.getAuthorUsername(),
                updatedComment.getText(),
                updatedComment.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
}