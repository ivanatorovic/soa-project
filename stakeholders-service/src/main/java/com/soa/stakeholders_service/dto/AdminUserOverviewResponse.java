package com.soa.stakeholders_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soa.stakeholders_service.model.UserRole;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminUserOverviewResponse {

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

    public AdminUserOverviewResponse() {
    }

    public AdminUserOverviewResponse(Long id, String username, String email, UserRole role, boolean blocked,
                                     String firstName, String lastName, String profileImage,
                                     String biography, String motto) {
        this.id = id;
        this.username = username;
        this.email =  email;
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }
}