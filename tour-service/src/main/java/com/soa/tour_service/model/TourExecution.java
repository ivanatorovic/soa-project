package com.soa.tour_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TourExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long touristId;

    @ManyToOne
    private Tour tour;

    @Enumerated(EnumType.STRING)
    private TourExecutionStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime abandonedAt;
    private LocalDateTime lastActivityAt;

    private Double startLatitude;
    private Double startLongitude;

    @OneToMany(mappedBy = "tourExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompletedKeyPoint> completedKeyPoints = new ArrayList<>();

    public Long getId() { return id; }

    public Long getTouristId() { return touristId; }
    public void setTouristId(Long touristId) { this.touristId = touristId; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public TourExecutionStatus getStatus() { return status; }
    public void setStatus(TourExecutionStatus status) { this.status = status; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getAbandonedAt() { return abandonedAt; }
    public void setAbandonedAt(LocalDateTime abandonedAt) { this.abandonedAt = abandonedAt; }

    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }

    public Double getStartLatitude() { return startLatitude; }
    public void setStartLatitude(Double startLatitude) { this.startLatitude = startLatitude; }

    public Double getStartLongitude() { return startLongitude; }
    public void setStartLongitude(Double startLongitude) { this.startLongitude = startLongitude; }

    public List<CompletedKeyPoint> getCompletedKeyPoints() {
        return completedKeyPoints;
    }

    public void setCompletedKeyPoints(List<CompletedKeyPoint> completedKeyPoints) {
        this.completedKeyPoints = completedKeyPoints;
    }
}