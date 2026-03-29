package com.soa.stakeholders_service.dto;

import com.soa.stakeholders_service.model.UserRole;

public class ProfileResponse {

    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private boolean blocked;

    private String firstName;
    private String lastName;
    private String profileImage;
    private String biography;
    private String motto;

    public ProfileResponse() {
    }

    public ProfileResponse(Long id, String username, String email, UserRole role, boolean blocked,
                           String firstName, String lastName, String profileImage,
                           String biography, String motto) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.blocked = blocked;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImage = profileImage;
        this.biography = biography;
        this.motto = motto;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getBiography() {
        return biography;
    }

    public String getMotto() {
        return motto;
    }
}