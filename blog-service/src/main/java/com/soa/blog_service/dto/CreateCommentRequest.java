package com.soa.blog_service.dto;

public class CreateCommentRequest {

    private String blogId;
    private String text;

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}