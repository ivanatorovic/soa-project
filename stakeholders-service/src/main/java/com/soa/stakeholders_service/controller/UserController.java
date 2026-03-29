package com.soa.stakeholders_service.controller;

import com.soa.stakeholders_service.dto.UserResponse;
import com.soa.stakeholders_service.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @PutMapping("/{id}/block")
    public UserResponse blockUser(@PathVariable Long id) {
        return userService.blockUser(id);
    }
}