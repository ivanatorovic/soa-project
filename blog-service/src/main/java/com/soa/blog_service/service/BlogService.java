package com.soa.blog_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soa.blog_service.dto.BlogResponse;
import com.soa.blog_service.dto.CreateBlogRequest;
import com.soa.blog_service.model.Blog;
import com.soa.blog_service.model.BlogLike;
import com.soa.blog_service.repository.BlogLikeRepository;
import com.soa.blog_service.repository.BlogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final BlogLikeRepository blogLikeRepository;
    private final ObjectMapper objectMapper;
    @Value("${app.upload-dir}")
    private String uploadDir;

    public BlogService(
            BlogRepository blogRepository,
            BlogLikeRepository blogLikeRepository,
            ObjectMapper objectMapper
    ) {
        this.blogRepository = blogRepository;
        this.blogLikeRepository = blogLikeRepository;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<?> createBlog(
            String infoJson,
            List<MultipartFile> images,
            Long authorId,
            String authorUsername,
            String role,
            HttpServletRequest request
    ) {
        try {
            if ("ADMIN".equalsIgnoreCase(role)) {
                return ResponseEntity.status(403)
                        .body(Map.of("message", "Admin ne može da objavljuje blogove"));
            }

            CreateBlogRequest req = objectMapper.readValue(infoJson, CreateBlogRequest.class);

            Blog blog = new Blog();
            blog.setTitle(req.getTitle());
            blog.setDescription(req.getDescription());
            blog.setAuthorId(authorId);
            blog.setAuthorUsername(authorUsername);

            List<String> imageUrls = new ArrayList<>();

            if (images != null && !images.isEmpty()) {
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                for (MultipartFile image : images) {
                    if (image == null || image.isEmpty()) {
                        continue;
                    }

                    String originalFilename = image.getOriginalFilename();
                    String extension = getFileExtension(originalFilename);
                    String uniqueFileName = UUID.randomUUID() + extension;

                    Path filePath = uploadPath.resolve(uniqueFileName);

                    Files.copy(
                            image.getInputStream(),
                            filePath,
                            StandardCopyOption.REPLACE_EXISTING
                    );

                    imageUrls.add("/assets/" + uniqueFileName);
                }
            }

            blog.setImageUrls(imageUrls);

            Blog savedBlog = blogRepository.save(blog);

            return ResponseEntity.ok(savedBlog);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Greška prilikom kreiranja bloga: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> getAllBlogs(Long userId) {
        List<Blog> blogs = blogRepository.findAll();

        if (blogs.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Nema blogova u sistemu"));
        }

        List<BlogResponse> response = blogs.stream()
                .map(blog -> mapToResponse(blog, userId))
                .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getBlogById(Long id, Long userId) {
        Blog blog = blogRepository.findById(id).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Ne postoji blog sa ID: " + id));
        }

        return ResponseEntity.ok(mapToResponse(blog, userId));
    }

    public ResponseEntity<?> likeBlog(Long blogId, Long userId, String role) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Morate proslediti userId"));
        }

        if ("ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Admin ne može da lajkuje blogove"));
        }

        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Ne postoji blog sa ID: " + blogId));
        }

        boolean alreadyLiked = blogLikeRepository.existsByBlogIdAndUserId(blogId, userId);

        if (alreadyLiked) {
            return ResponseEntity.badRequest().body(Map.of("message", "Korisnik je vec lajkovao ovaj blog"));
        }

        BlogLike like = new BlogLike();
        like.setBlog(blog);
        like.setUserId(userId);

        blogLikeRepository.save(like);

        return ResponseEntity.ok(Map.of("message", "Blog je uspesno lajkovan"));
    }

    public ResponseEntity<?> unlikeBlog(Long blogId, Long userId, String role) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Morate proslediti userId"));
        }

        if ("ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Admin ne može da uklanja lajk sa blogova"));
        }

        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Ne postoji blog sa ID: " + blogId));
        }

        BlogLike like = blogLikeRepository.findByBlogIdAndUserId(blogId, userId).orElse(null);

        if (like == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Korisnik nije lajkovao ovaj blog"));
        }

        blogLikeRepository.delete(like);

        return ResponseEntity.ok(Map.of("message", "Like je uspesno uklonjen"));
    }

    public ResponseEntity<?> getLikesCount(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Ne postoji blog sa ID: " + blogId));
        }

        long count = blogLikeRepository.countByBlogId(blogId);
        return ResponseEntity.ok(count);
    }

    private BlogResponse mapToResponse(Blog blog, Long userId) {
        BlogResponse response = new BlogResponse();
        response.setId(blog.getId());
        response.setTitle(blog.getTitle());
        response.setDescription(blog.getDescription());
        response.setCreatedAt(blog.getCreatedAt());
        response.setImageUrls(blog.getImageUrls());
        response.setAuthorUsername(blog.getAuthorUsername());
        response.setLikesCount((int) blogLikeRepository.countByBlogId(blog.getId()));

        boolean likedByCurrentUser = false;
        if (userId != null) {
            likedByCurrentUser = blogLikeRepository.existsByBlogIdAndUserId(blog.getId(), userId);
        }

        response.setLikedByCurrentUser(likedByCurrentUser);

        return response;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}