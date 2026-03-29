package com.soa.blog_service.dto;

public class CreateCommentRequest {

    private Long blogId;
    private String text;

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}