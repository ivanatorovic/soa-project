package com.soa.stakeholders_service.dto;

import com.soa.stakeholders_service.model.UserRole;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private boolean blocked;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String email, UserRole role, boolean blocked) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.blocked = blocked;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isBlocked() {
        return blocked;
    }
}