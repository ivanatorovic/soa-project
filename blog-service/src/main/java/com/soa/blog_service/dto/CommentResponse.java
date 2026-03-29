package com.soa.blog_service.dto;

public class CommentResponse {

    private Long id;
    private Long blogId;
    private String authorUsername;
    private String text;

    public CommentResponse() {
    }

    public CommentResponse(Long id, Long blogId, String authorUsername, String text) {
        this.id = id;
        this.blogId = blogId;
        this.authorUsername = authorUsername;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public Long getBlogId() {
        return blogId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getText() {
        return text;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public void setText(String text) {
        this.text = text;
    }
}