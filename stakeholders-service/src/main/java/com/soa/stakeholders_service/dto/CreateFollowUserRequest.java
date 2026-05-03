package com.soa.stakeholders_service.dto;

public class CreateFollowUserRequest {
    private Long userId;
    private String username;

    public CreateFollowUserRequest() {
    }

    public CreateFollowUserRequest(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}