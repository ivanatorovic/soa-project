package com.soa.stakeholders_service.service;

import com.soa.stakeholders_service.dto.CreateFollowUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FollowerClientService {

    private final RestTemplate restTemplate;

    @Value("${follower.service.url}")
    private String followerServiceUrl;

    public FollowerClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createUserNode(Long userId, String username) {
        String url = followerServiceUrl + "/api/follows/users";

        CreateFollowUserRequest request = new CreateFollowUserRequest(userId, username);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to create user node in follower-service.");
        }
    }
}