package com.soa.blog_service.repository;

import com.soa.blog_service.model.BlogLike;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BlogLikeRepository extends MongoRepository<BlogLike, String> {
    boolean existsByBlogIdAndUserId(String blogId, Long userId);
    Optional<BlogLike> findByBlogIdAndUserId(String blogId, Long userId);
    long countByBlogId(String blogId);
}