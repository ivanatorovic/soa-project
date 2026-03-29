package com.soa.stakeholders_service.service;

import com.soa.stakeholders_service.dto.UserResponse;
import com.soa.stakeholders_service.exception.BadRequestException;
import com.soa.stakeholders_service.exception.ResourceNotFoundException;
import com.soa.stakeholders_service.model.User;
import com.soa.stakeholders_service.model.UserRole;
import com.soa.stakeholders_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found."));

        return mapToResponse(user);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isBlocked()
        );
    }
}