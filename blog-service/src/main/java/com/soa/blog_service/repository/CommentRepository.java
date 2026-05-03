package com.soa.blog_service.repository;

import com.soa.blog_service.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByBlogIdOrderByCreatedAtDesc(String blogId);
}