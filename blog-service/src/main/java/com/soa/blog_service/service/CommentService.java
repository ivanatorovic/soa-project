package com.soa.blog_service.service;

import com.soa.blog_service.dto.CreateCommentRequest;
import com.soa.blog_service.model.Blog;
import com.soa.blog_service.model.Comment;
import com.soa.blog_service.repository.BlogRepository;
import com.soa.blog_service.repository.CommentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;

    public CommentService(CommentRepository commentRepository, BlogRepository blogRepository) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
    }

    public ResponseEntity<?> createComment(CreateCommentRequest request) {
        Blog blog = blogRepository.findById(request.getBlogId()).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body("Ne postoji blog sa ID: " + request.getBlogId());
        }

        if (request.getText() == null || request.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tekst komentara ne sme biti prazan");
        }

        Comment comment = new Comment();
        comment.setBlog(blog);
        comment.setAuthorId(request.getAuthorId());
        comment.setText(request.getText());

        return ResponseEntity.ok(commentRepository.save(comment));
    }

    public ResponseEntity<?> getCommentsByBlogId(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body("Ne postoji blog sa ID: " + blogId);
        }

        List<Comment> comments = commentRepository.findByBlogId(blogId);

        if (comments.isEmpty()) {
            return ResponseEntity.status(404).body("Nema komentara za ovaj blog");
        }

        return ResponseEntity.ok(comments);
    }

    public ResponseEntity<?> updateComment(Long id, String newText) {
        Comment comment = commentRepository.findById(id).orElse(null);

        if (comment == null) {
            return ResponseEntity.status(404).body("Ne postoji komentar sa ID: " + id);
        }

        if (newText == null || newText.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tekst komentara ne sme biti prazan");
        }

        comment.setText(newText);
        return ResponseEntity.ok(commentRepository.save(comment));
    }
}