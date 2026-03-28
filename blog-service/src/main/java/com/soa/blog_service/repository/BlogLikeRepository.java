package com.soa.blog_service.repository;

import com.soa.blog_service.model.BlogLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogLikeRepository extends JpaRepository<BlogLike, Long> {
    boolean existsByBlogIdAndUserId(Long blogId, Long userId);
    Optional<BlogLike> findByBlogIdAndUserId(Long blogId, Long userId);
    long countByBlogId(Long blogId);
}