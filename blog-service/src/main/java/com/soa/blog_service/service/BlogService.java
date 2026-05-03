package com.soa.blog_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soa.blog_service.dto.BlogResponse;
import com.soa.blog_service.dto.CreateBlogRequest;
import com.soa.blog_service.dto.FollowUserDto;
import com.soa.blog_service.model.Blog;
import com.soa.blog_service.model.BlogLike;
import com.soa.blog_service.repository.BlogLikeRepository;
import com.soa.blog_service.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.ParameterizedTypeReference;

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

    @Value("${app.upload-dir}")
    private String uploadDir;

    private final BlogRepository blogRepository;
    private final BlogLikeRepository blogLikeRepository;
    private final ObjectMapper objectMapper;
    @Value("${follower.service.url}")
    private String followerServiceUrl;

    private final RestTemplate restTemplate;

    public BlogService(
            BlogRepository blogRepository,
            BlogLikeRepository blogLikeRepository,
            ObjectMapper objectMapper,
            RestTemplate restTemplate
    ) {
        this.blogRepository = blogRepository;
        this.blogLikeRepository = blogLikeRepository;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> createBlog(
            String infoJson,
            List<MultipartFile> images,
            Long authorId,
            String authorUsername,
            String role
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
            blog.prePersist();

            List<String> imageNames = new ArrayList<>();

            if (images != null && !images.isEmpty()) {
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                for (MultipartFile image : images) {
                    if (image == null || image.isEmpty()) continue;
                    String originalFilename = image.getOriginalFilename();
                    String extension = getFileExtension(originalFilename);
                    String uniqueFileName = UUID.randomUUID() + extension;
                    Path filePath = uploadPath.resolve(uniqueFileName);
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    imageUrls.add("/uploads/" + uniqueFileName);
                }
            }

            blog.setImageUrls(imageUrls);

            Blog savedBlog = blogRepository.save(blog);

            return ResponseEntity.ok(savedBlog);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Greška prilikom kreiranja bloga: " + e.getMessage()));
        }
    }

    public ResponseEntity<?> getAllBlogs(Long userId) {
        List<Blog> blogs = blogRepository.findAll();

        if (blogs.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Nema blogova u sistemu"));
        }
        List<BlogResponse> response = blogs.stream().map(blog -> mapToResponse(blog, userId)).toList();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getBlogById(String id, Long userId) {
        Blog blog = blogRepository.findById(id).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Ne postoji blog sa ID: " + id));
        }

        return ResponseEntity.ok(mapToResponse(blog, userId));
    }

    public ResponseEntity<?> likeBlog(String blogId, Long userId,String role) {
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Morate proslediti userId"));
        }

        if ("ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Admin ne može da lajkuje blogove"));
        }

        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Ne postoji blog sa ID: " + blogId));
        }

        boolean alreadyLiked = blogLikeRepository.existsByBlogIdAndUserId(blogId, userId);

        if (alreadyLiked) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Korisnik je vec lajkovao ovaj blog"));
        }

        BlogLike like = new BlogLike();
        like.setBlogId(blogId);
        like.setUserId(userId);
        like.prePersist();
        blogLikeRepository.save(like);

        return ResponseEntity.ok(Map.of("message", "Blog je uspesno lajkovan"));
    }

    public ResponseEntity<?> unlikeBlog(String blogId, Long userId,String role) {
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Morate proslediti userId"));
        }

        if ("ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Admin ne može da uklanja lajk sa blogova"));
        }

        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Ne postoji blog sa ID: " + blogId));
        }

        BlogLike like = blogLikeRepository.findByBlogIdAndUserId(blogId, userId).orElse(null);

        if (like == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Korisnik nije lajkovao ovaj blog"));
        }

        blogLikeRepository.delete(like);

        return ResponseEntity.ok(Map.of("message", "Like je uspesno uklonjen"));
    }

    public ResponseEntity<?> getLikesCount(String blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Ne postoji blog sa ID: " + blogId));
        }

        long count = blogLikeRepository.countByBlogId(blogId);
        return ResponseEntity.ok(count);
    }

    public ResponseEntity<byte[]> getBlogImage(Long blogId, int imageIndex) {
        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.notFound().build();
        }

        List<String> imageNames = blog.getImageUrls();

        if (imageNames == null || imageIndex < 0 || imageIndex >= imageNames.size()) {
            return ResponseEntity.notFound().build();
        }

        String fileName = imageNames.get(imageIndex);

        try {
            Path path = resolveImagePath(fileName);

            System.out.println("UPLOAD DIR = " + Paths.get(uploadDir).toAbsolutePath().normalize());
            System.out.println("FILE NAME = " + fileName);
            System.out.println("FULL IMAGE PATH = " + path);
            System.out.println("EXISTS = " + Files.exists(path));

            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(path);

            return ResponseEntity.ok()
                    .contentType(getImageMediaType(fileName))
                    .body(imageBytes);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    private BlogResponse mapToResponse(Blog blog, Long userId) {
        BlogResponse response = new BlogResponse();
        response.setId(blog.getId());
        response.setTitle(blog.getTitle());
        response.setDescription(blog.getDescription());
        response.setCreatedAt(blog.getCreatedAt());
        response.setImageUrls(blog.getImageUrls());
        response.setAuthorUsername(blog.getAuthorUsername());
        response.setAuthorId(blog.getAuthorId());
        response.setLikesCount((int) blogLikeRepository.countByBlogId(blog.getId()));

        List<String> imageEndpoints = new ArrayList<>();
        List<String> storedImageNames = blog.getImageUrls();

        if (storedImageNames != null) {
            for (int i = 0; i < storedImageNames.size(); i++) {
                imageEndpoints.add("/api/blogs/" + blog.getId() + "/images/" + i);
            }
        }

        response.setImageUrls(imageEndpoints);

        boolean likedByCurrentUser = false;
        if (userId != null) {
            likedByCurrentUser = blogLikeRepository.existsByBlogIdAndUserId(blog.getId(), userId);
        }

        response.setLikedByCurrentUser(likedByCurrentUser);

        return response;
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String uniqueFileName = UUID.randomUUID() + extension;

        Path filePath = uploadPath.resolve(uniqueFileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("UPLOAD DIR = " + uploadPath);
        System.out.println("SAVED FILE PATH = " + filePath);

        return uniqueFileName;
    }

    private Path resolveImagePath(String fileName) {
        return Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize()
                .resolve(fileName);
    }

    private MediaType getImageMediaType(String filename) {
        String lower = filename.toLowerCase();

        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lower.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }

    public ResponseEntity<?> getFollowedUsersBlogs(Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Morate proslediti userId"));
        }

        try {
            String url = followerServiceUrl + "/api/follows/" + userId + "/following";
            ResponseEntity<List<FollowUserDto>> followResponse = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<FollowUserDto>>() {}
            );

            List<FollowUserDto> followedUsers = followResponse.getBody();

            if (followedUsers == null || followedUsers.isEmpty()) {
                return ResponseEntity.ok(
                        Map.of("message", "Ne pratite nijednog korisnika")
                );
            }

            List<Long> followedIds = followedUsers.stream()
                    .map(FollowUserDto::getUserId)
                    .toList();

            List<Blog> blogs = blogRepository.findByAuthorIdIn(followedIds);

            if (blogs.isEmpty()) {
                return ResponseEntity.ok(
                        Map.of("message", "Korisnici koje pratite nemaju blogove")
                );
            }

            List<BlogResponse> response = blogs.stream()
                    .map(blog -> mapToResponse(blog, userId))
                    .toList();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Greska pri pozivu follower servisa: " + e.getMessage()));
        }
    }
}