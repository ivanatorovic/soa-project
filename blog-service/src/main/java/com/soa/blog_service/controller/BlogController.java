package com.soa.blog_service.controller;

import com.soa.blog_service.dto.CreateBlogRequest;
import com.soa.blog_service.security.JwtUserPrincipal;
import com.soa.blog_service.service.BlogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping
    public ResponseEntity<?> createBlog(
            @RequestBody CreateBlogRequest request,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return blogService.createBlog(
                request,
                principal.getUserId(),
                principal.getUsername()
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllBlogs() {
        return blogService.getAllBlogs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable Long id) {
        return blogService.getBlogById(id);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeBlog(
            @PathVariable Long id,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return blogService.likeBlog(id, principal.getUserId());
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlikeBlog(
            @PathVariable Long id,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return blogService.unlikeBlog(id, principal.getUserId());
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<?> getLikesCount(@PathVariable Long id) {
        return blogService.getLikesCount(id);
    }
}