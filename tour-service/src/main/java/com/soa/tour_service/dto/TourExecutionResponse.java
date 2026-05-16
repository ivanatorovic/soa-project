package com.soa.tour_service.dto;

import com.soa.tour_service.model.TourExecutionStatus;

import java.time.LocalDateTime;
import java.util.List;

public class TourExecutionResponse {

    private Long id;
    private Long tourId;
    private String tourName;
    private TourExecutionStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime abandonedAt;
    private LocalDateTime lastActivityAt;

    private List<CompletedKeyPointResponse> completedKeyPoints;

    public TourExecutionResponse(
            Long id,
            Long tourId,
            String tourName,
            TourExecutionStatus status,
            LocalDateTime startedAt,
            LocalDateTime completedAt,
            LocalDateTime abandonedAt,
            LocalDateTime lastActivityAt,
            List<CompletedKeyPointResponse> completedKeyPoints
    ) {
        this.id = id;
        this.tourId = tourId;
        this.tourName = tourName;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.abandonedAt = abandonedAt;
        this.lastActivityAt = lastActivityAt;
        this.completedKeyPoints = completedKeyPoints;
    }

    public Long getId() { return id; }
    public Long getTourId() { return tourId; }
    public String getTourName() { return tourName; }
    public TourExecutionStatus getStatus() { return status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public LocalDateTime getAbandonedAt() { return abandonedAt; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public List<CompletedKeyPointResponse> getCompletedKeyPoints() { return completedKeyPoints; }
}