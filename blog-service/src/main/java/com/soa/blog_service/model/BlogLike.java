package com.soa.blog_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "blog_likes")
public class BlogLike {

    @Id
    private String id;

    private String blogId;
    private Long userId;
    private LocalDateTime createdAt;

    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getBlogId() { return blogId; }
    public void setBlogId(String blogId) { this.blogId = blogId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}