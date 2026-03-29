package com.soa.stakeholders_service.controller;

import com.soa.stakeholders_service.dto.*;
import com.soa.stakeholders_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.soa.stakeholders_service.dto.UpdateProfileRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public AdminUserOverviewResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<AdminUserOverviewResponse> getAll() {
        return userService.getAll();
    }

    @PutMapping("/{id}/block")
    public UserResponse blockUser(@PathVariable Long id) {
        return userService.blockUser(id);
    }

    @GetMapping("/me/profile")
    public AdminUserOverviewResponse getMyProfile(Authentication authentication) {
        return userService.getMyProfile(authentication.getName());
    }

    @PatchMapping("/me/profile")
    public ProfileResponse updateMyProfile(
            Authentication authentication,
            @RequestBody Map<String, Object> request
    ) {
        return userService.updateMyProfile(authentication.getName(), request);
    }

}