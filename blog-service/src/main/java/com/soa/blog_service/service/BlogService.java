package com.soa.blog_service.service;

import com.soa.blog_service.dto.BlogResponse;
import com.soa.blog_service.dto.CreateBlogRequest;
import com.soa.blog_service.model.Blog;
import com.soa.blog_service.model.BlogLike;
import com.soa.blog_service.repository.BlogLikeRepository;
import com.soa.blog_service.repository.BlogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final BlogLikeRepository blogLikeRepository;

    public BlogService(BlogRepository blogRepository, BlogLikeRepository blogLikeRepository) {
        this.blogRepository = blogRepository;
        this.blogLikeRepository = blogLikeRepository;
    }

    public ResponseEntity<?> createBlog(CreateBlogRequest request, Long authorId) {
        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setDescription(request.getDescription());
        blog.setAuthorId(authorId);
        blog.setImageUrl(request.getImageUrl());

        blogRepository.save(blog);

        return ResponseEntity.ok(blog);
    }

    public ResponseEntity<?> getAllBlogs() {
        List<Blog> blogs = blogRepository.findAll();

        if (blogs.isEmpty()) {
            return ResponseEntity.status(404).body("Nema blogova u sistemu");
        }

        List<BlogResponse> response = blogs.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getBlogById(Long id) {
        Blog blog = blogRepository.findById(id).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body("Ne postoji blog sa ID: " + id);
        }

        return ResponseEntity.ok(mapToResponse(blog));
    }

    public ResponseEntity<?> likeBlog(Long blogId, Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("Morate proslediti userId");
        }

        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body("Ne postoji blog sa ID: " + blogId);
        }

        boolean alreadyLiked = blogLikeRepository.existsByBlogIdAndUserId(blogId, userId);

        if (alreadyLiked) {
            return ResponseEntity.badRequest().body("Korisnik je vec lajkovao ovaj blog");
        }

        BlogLike like = new BlogLike();
        like.setBlog(blog);
        like.setUserId(userId);

        blogLikeRepository.save(like);

        return ResponseEntity.ok("Blog je uspesno lajkovan");
    }

    public ResponseEntity<?> unlikeBlog(Long blogId, Long userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("Morate proslediti userId");
        }

        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body("Ne postoji blog sa ID: " + blogId);
        }

        BlogLike like = blogLikeRepository.findByBlogIdAndUserId(blogId, userId).orElse(null);

        if (like == null) {
            return ResponseEntity.status(404).body("Korisnik nije lajkovao ovaj blog");
        }

        blogLikeRepository.delete(like);
        return ResponseEntity.ok("Like je uspesno uklonjen");
    }

    public ResponseEntity<?> getLikesCount(Long blogId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);

        if (blog == null) {
            return ResponseEntity.status(404).body("Ne postoji blog sa ID: " + blogId);
        }

        long count = blogLikeRepository.countByBlogId(blogId);
        return ResponseEntity.ok(count);
    }

    private BlogResponse mapToResponse(Blog blog) {
        BlogResponse response = new BlogResponse();
        response.setId(blog.getId());
        response.setTitle(blog.getTitle());
        response.setDescription(blog.getDescription());
        response.setCreatedAt(blog.getCreatedAt());
        response.setImageUrl(blog.getImageUrl());
        response.setAuthorId(blog.getAuthorId());
        response.setLikesCount((int) blogLikeRepository.countByBlogId(blog.getId()));
        return response;
    }
}