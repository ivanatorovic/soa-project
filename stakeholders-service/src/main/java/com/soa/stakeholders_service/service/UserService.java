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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private static final String PROFILE_IMAGE_DIR = "storage/profile-images";

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
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

    public ProfileResponse updateMyProfile(String username, String infoJson, MultipartFile profileImage) {
        final UpdateProfileRequest request;

        try {
            request = objectMapper.readValue(infoJson, UpdateProfileRequest.class);
        } catch (IOException e) {
            throw new BadRequestException("Invalid JSON in field 'info'.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with username " + username + " not found."
                ));

        if (user.getRole() == UserRole.ADMIN) {
            throw new BadRequestException("Admin cannot update profile details.");
        }

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (request.getUsername().length() > 100) {
                throw new BadRequestException("Username must be at most 100 characters.");
            }

            userRepository.findByUsername(request.getUsername()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(user.getId())) {
                    throw new BadRequestException("Username already exists.");
                }
            });

            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (request.getEmail().length() > 255) {
                throw new BadRequestException("Email must be at most 255 characters.");
            }

            userRepository.findByEmail(request.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(user.getId())) {
                    throw new BadRequestException("Email is already taken.");
                }
            });

            user.setEmail(request.getEmail());
        }

        boolean currentPasswordProvided = request.getCurrentPassword() != null && !request.getCurrentPassword().isBlank();
        boolean newPasswordProvided = request.getNewPassword() != null && !request.getNewPassword().isBlank();

        if (currentPasswordProvided || newPasswordProvided) {
            if (!currentPasswordProvided || !newPasswordProvided) {
                throw new BadRequestException("Both current password and new password are required.");
            }

            if (request.getCurrentPassword().length() < 1 || request.getCurrentPassword().length() > 255) {
                throw new BadRequestException("Current password must be between 1 and 255 characters.");
            }

            if (request.getNewPassword().length() < 1 || request.getNewPassword().length() > 255) {
                throw new BadRequestException("New password must be between 1 and 255 characters.");
            }

            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BadRequestException("Current password is incorrect.");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        if (request.getFirstName() != null) {
            if (request.getFirstName().length() > 100) {
                throw new BadRequestException("First name must not exceed 100 characters.");
            }
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            if (request.getLastName().length() > 100) {
                throw new BadRequestException("Last name must not exceed 100 characters.");
            }
            user.setLastName(request.getLastName());
        }

        if (request.getBiography() != null) {
            if (request.getBiography().length() > 2000) {
                throw new BadRequestException("Biography must not exceed 2000 characters.");
            }
            user.setBiography(request.getBiography());
        }

        if (request.getMotto() != null) {
            if (request.getMotto().length() > 255) {
                throw new BadRequestException("Motto must not exceed 255 characters.");
            }
            user.setMotto(request.getMotto());
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String contentType = profileImage.getContentType();

            if (contentType == null ||
                    (!contentType.equals("image/jpeg")
                            && !contentType.equals("image/jpg")
                            && !contentType.equals("image/png")
                            && !contentType.equals("image/webp"))) {
                throw new BadRequestException("Only JPG, JPEG, PNG and WEBP images are allowed.");
            }

            if (profileImage.getSize() > 5 * 1024 * 1024) {
                throw new BadRequestException("Profile image must not exceed 5MB.");
            }

            try {
                String savedImagePath = saveProfileImage(profileImage);
                user.setProfileImage(savedImagePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save profile image.", e);
            }
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
        if (user.getRole() == UserRole.ADMIN) {
            return new ProfileResponse(
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

        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isBlocked(),
                user.getFirstName() != null ? user.getFirstName() : "",
                user.getLastName() != null ? user.getLastName() : "",
                user.getProfileImage() != null ? user.getProfileImage() : "",
                user.getBiography() != null ? user.getBiography() : "",
                user.getMotto() != null ? user.getMotto() : ""
        );
    }

    private String saveProfileImage(MultipartFile file) throws IOException {
        Path dirPath = Paths.get(System.getProperty("user.dir"), "storage", "profile-images")
                .toAbsolutePath()
                .normalize();
        Files.createDirectories(dirPath);

        String extension = "";
        String originalFilename = file.getOriginalFilename();

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID() + extension;
        Path fullPath = dirPath.resolve(filename);

        Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);

        return "/profile-images/" + filename;
    }
}