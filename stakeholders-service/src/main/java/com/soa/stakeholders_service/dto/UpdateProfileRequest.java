package com.soa.stakeholders_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @Size(max = 100, message = "Username must be at most 100 characters.")
    private String username;

    @Email(message = "Email must be valid.")
    @Size(max = 255, message = "Email must be at most 255 characters.")
    private String email;

    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters.")
    private String password;

    @Size(max = 100, message = "First name must be at most 100 characters.")
    private String firstName;

    @Size(max = 100, message = "Last name must be at most 100 characters.")
    private String lastName;

    @Size(max = 500, message = "Profile image must be at most 500 characters.")
    private String profileImage;

    @Size(max = 2000, message = "Biography must be at most 2000 characters.")
    private String biography;

    @Size(max = 255, message = "Motto must be at most 255 characters.")
    private String motto;

    public UpdateProfileRequest() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }
}