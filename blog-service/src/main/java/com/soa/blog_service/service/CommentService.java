package com.soa.blog_service.service;

import com.soa.blog_service.dto.CommentResponse;
import com.soa.blog_service.dto.CreateCommentRequest;
import com.soa.blog_service.dto.FollowUserDto;
import com.soa.blog_service.model.Blog;
import com.soa.blog_service.model.Comment;
import com.soa.blog_service.repository.BlogRepository;
import com.soa.blog_service.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final RestTemplate restTemplate;

    @Value("${follower.service.url}")
    private String followerServiceUrl;

    public CommentService(
            CommentRepository commentRepository,
            BlogRepository blogRepository,
            RestTemplate restTemplate
    ) {
        this.commentRepository = commentRepository;
        this.blogRepository = blogRepository;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> createComment(
            CreateCommentRequest request,
            Long authorId,
            String authorUsername,
            String role
    ) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Admin ne može da objavljuje komentare"));
        }

        Blog blog = blogRepository.findById(request.getBlogId()).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Ne postoji blog sa ID: " + request.getBlogId()));
        }

        if (!blog.getAuthorId().equals(authorId)) {
            try {
                String url = followerServiceUrl + "/api/follows/" + authorId + "/following";

                ResponseEntity<List<FollowUserDto>> followResponse = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<FollowUserDto>>() {}
                );

                List<FollowUserDto> followedUsers = followResponse.getBody();

                boolean followsAuthor = followedUsers != null &&
                        followedUsers.stream()
                                .anyMatch(f -> f.getUserId().equals(blog.getAuthorId()));

                if (!followsAuthor) {
                    return ResponseEntity.status(403)
                            .body(Map.of(
                                    "message",
                                    "Morate zapratiti korisnika da biste komentarisali blog."
                            ));
                }

            } catch (Exception e) {
                return ResponseEntity.status(500)
                        .body(Map.of("message", "Greška pri proveri praćenja korisnika"));
            }
        }

        if (request.getText() == null || request.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Tekst komentara ne sme biti prazan"));
        }

        Comment comment = new Comment();
        comment.setBlogId(blog.getId());
        comment.setAuthorId(authorId);
        comment.setAuthorUsername(authorUsername);
        comment.setText(request.getText());
        comment.prePersist();

        Comment savedComment = commentRepository.save(comment);

        CommentResponse response = new CommentResponse(
                savedComment.getId(),
                savedComment.getBlogId(),
                savedComment.getAuthorId(),
                savedComment.getAuthorUsername(),
                savedComment.getText(),
                savedComment.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getCommentsByBlogId(String blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Ne postoji blog sa ID: " + blogId));
        }

        List<Comment> comments = commentRepository.findByBlogIdOrderByCreatedAtDesc(blogId);

        if (comments.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Nema komentara za ovaj blog"));
        }

        List<CommentResponse> response = comments.stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getBlogId(),
                        comment.getAuthorId(),
                        comment.getAuthorUsername(),
                        comment.getText(),
                        comment.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> updateComment(
            String id,
            String newText,
            Long currentUserId,
            String role
    ) {
        Comment comment = commentRepository.findById(id).orElse(null);

        if ("ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Admin ne može da menja komentare"));
        }

        if (comment == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Ne postoji komentar sa ID: " + id));
        }

        if (!comment.getAuthorId().equals(currentUserId)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Mozete menjati samo svoj komentar"));
        }

        if (newText == null || newText.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Tekst komentara ne sme biti prazan"));
        }

        comment.setText(newText);
        comment.preUpdate();

        Comment updatedComment = commentRepository.save(comment);

        CommentResponse response = new CommentResponse(
                updatedComment.getId(),
                updatedComment.getBlogId(),
                updatedComment.getAuthorId(),
                updatedComment.getAuthorUsername(),
                updatedComment.getText(),
                updatedComment.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
}