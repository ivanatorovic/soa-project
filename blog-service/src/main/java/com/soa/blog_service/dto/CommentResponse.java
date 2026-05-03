package com.soa.blog_service.dto;

import java.time.LocalDateTime;

public class CommentResponse {

    private String id;
    private String blogId;
    private String authorUsername;
    private Long authorId;
    private String text;
    private LocalDateTime createdAt;

    public CommentResponse() {}

    public CommentResponse(String id, String blogId,Long authorId, String authorUsername, String text, LocalDateTime createdAt) {
        this.id = id;
        this.blogId = blogId;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBlogId() { return blogId; }
    public void setBlogId(String blogId) { this.blogId = blogId; }
    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

}