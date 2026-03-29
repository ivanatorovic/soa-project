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
import org.springframework.stereotype.Service;
import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<AdminUserOverviewResponse> getAll() {
        return userRepository.findAll()
                .stream()
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

    public AdminUserOverviewResponse getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found."));

        return mapToAdminUserOverviewResponse(user);
    }


    public ProfileResponse updateMyProfile(String username, Map<String, Object> request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found."));


        List<String> allowedFields = List.of(
                "firstName",
                "lastName",
                "profileImage",
                "biography",
                "motto"
        );


        for (String key : request.keySet()) {
            if (!allowedFields.contains(key)) {
                throw new BadRequestException("Ne možete da menjate ove podatke.");
            }
        }


        if (request.containsKey("firstName")) {
            user.setFirstName((String) request.get("firstName"));
        }

        if (request.containsKey("lastName")) {
            user.setLastName((String) request.get("lastName"));
        }

        if (request.containsKey("profileImage")) {
            user.setProfileImage((String) request.get("profileImage"));
        }

        if (request.containsKey("biography")) {
            user.setBiography((String) request.get("biography"));
        }

        if (request.containsKey("motto")) {
            user.setMotto((String) request.get("motto"));
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