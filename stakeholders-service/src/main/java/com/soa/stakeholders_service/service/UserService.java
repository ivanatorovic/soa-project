package com.soa.stakeholders_service.service;

import com.soa.stakeholders_service.dto.AdminUserOverviewResponse;
import com.soa.stakeholders_service.dto.ProfileResponse;
import com.soa.stakeholders_service.dto.UpdateProfileRequest;
import com.soa.stakeholders_service.dto.UserResponse;
import com.soa.stakeholders_service.exception.BadRequestException;
import com.soa.stakeholders_service.exception.ResourceNotFoundException;
import com.soa.stakeholders_service.model.User;
import com.soa.stakeholders_service.model.UserRole;
import com.soa.stakeholders_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public List<AdminUserOverviewResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == UserRole.GUIDE || user.getRole() == UserRole.TOURIST)
                .map(this::mapToAdminUserOverviewResponse)
                .collect(Collectors.toList());
    }

    public AdminUserOverviewResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found."));

        return mapToAdminUserOverviewResponse(user);
    }

    private AdminUserOverviewResponse mapToAdminUserOverviewResponse(User user) {
        if (user.getRole() == UserRole.ADMIN) {
            return new AdminUserOverviewResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.isBlocked(),
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        return new AdminUserOverviewResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isBlocked(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImage(),
                user.getBiography(),
                user.getMotto()
        );
    }

    public UserResponse blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found."));

        if (user.getRole() == UserRole.ADMIN) {
            throw new BadRequestException("Only accounts created by users (GUIDE or TOURIST) can be blocked.");
        }

        if (user.isBlocked()) {
            throw new BadRequestException("User with id " + id + " is already blocked.");
        }

        user.setBlocked(true);
        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public ProfileResponse getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with username " + username + " not found."
                ));

        return mapToProfileResponse(user);
    }

    public ProfileResponse updateMyProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with username " + username + " not found."
                ));

        if (request.getUsername() != null) {
            String newUsername = request.getUsername();
            if (!newUsername.equals(user.getUsername()) &&
                    userRepository.existsByUsername(newUsername)) {
                throw new BadRequestException("Username is already taken.");
            }
            user.setUsername(newUsername);
        }

        if (request.getEmail() != null) {
            String newEmail = request.getEmail();
            if (!newEmail.equals(user.getEmail()) &&
                    userRepository.existsByEmail(newEmail)) {
                throw new BadRequestException("Email is already taken.");
            }
            user.setEmail(newEmail);
        }

        if (request.getPassword() != null) {
            if (request.getPassword().isBlank()) {
                throw new BadRequestException("Password cannot be empty.");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }

        if (request.getBiography() != null) {
            user.setBiography(request.getBiography());
        }

        if (request.getMotto() != null) {
            user.setMotto(request.getMotto());
        }

        User savedUser = userRepository.save(user);
        return mapToProfileResponse(savedUser);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isBlocked()
        );
    }

    private ProfileResponse mapToProfileResponse(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isBlocked(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImage(),
                user.getBiography(),
                user.getMotto()
        );
    }
}