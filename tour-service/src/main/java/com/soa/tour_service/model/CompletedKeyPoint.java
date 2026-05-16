package com.soa.tour_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CompletedKeyPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long keyPointId;
    private String keyPointName;

    private LocalDateTime reachedAt;

    @ManyToOne
    private TourExecution tourExecution;

    public Long getId() { return id; }

    public Long getKeyPointId() { return keyPointId; }
    public void setKeyPointId(Long keyPointId) { this.keyPointId = keyPointId; }

    public String getKeyPointName() { return keyPointName; }
    public void setKeyPointName(String keyPointName) { this.keyPointName = keyPointName; }

    public LocalDateTime getReachedAt() { return reachedAt; }
    public void setReachedAt(LocalDateTime reachedAt) { this.reachedAt = reachedAt; }

    public TourExecution getTourExecution() { return tourExecution; }
    public void setTourExecution(TourExecution tourExecution) { this.tourExecution = tourExecution; }
}