package com.soa.tour_service.dto;

import java.time.LocalDateTime;

public class CompletedKeyPointResponse {

    private Long keyPointId;
    private String keyPointName;
    private LocalDateTime reachedAt;

    public CompletedKeyPointResponse(Long keyPointId, String keyPointName, LocalDateTime reachedAt) {
        this.keyPointId = keyPointId;
        this.keyPointName = keyPointName;
        this.reachedAt = reachedAt;
    }

    public Long getKeyPointId() {
        return keyPointId;
    }

    public String getKeyPointName() {
        return keyPointName;
    }

    public LocalDateTime getReachedAt() {
        return reachedAt;
    }
}