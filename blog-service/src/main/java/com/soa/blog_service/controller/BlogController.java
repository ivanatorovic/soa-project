package com.soa.blog_service.controller;

import com.soa.blog_service.security.JwtUserPrincipal;
import com.soa.blog_service.service.BlogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBlog(
            @RequestPart("info") String infoJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            HttpServletRequest request
    ) {
        return blogService.createBlog(infoJson, images, principal.getUserId(), principal.getUsername(), request);
    }

    @GetMapping
    public ResponseEntity<?> getAllBlogs(@AuthenticationPrincipal JwtUserPrincipal principal) {
        Long userId = principal != null ? principal.getUserId() : null;
        return blogService.getAllBlogs(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(
            @PathVariable String id,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        Long userId = principal != null ? principal.getUserId() : null;
        return blogService.getBlogById(id, userId);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeBlog(
            @PathVariable String id,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return blogService.likeBlog(id, principal.getUserId());
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlikeBlog(
            @PathVariable String id,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        return blogService.unlikeBlog(id, principal.getUserId());
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<?> getLikesCount(@PathVariable String id) {
        return blogService.getLikesCount(id);
    }
}