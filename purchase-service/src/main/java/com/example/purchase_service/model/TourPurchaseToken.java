package com.example.purchase_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TourPurchaseToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long touristId;
    private Long tourId;
    private String token;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private TourPurchaseTokenStatus status = TourPurchaseTokenStatus.AVAILABLE;

    public TourPurchaseToken() {}

    public Long getId() { return id; }
    public Long getTouristId() { return touristId; }
    public Long getTourId() { return tourId; }
    public String getToken() { return token; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public TourPurchaseTokenStatus getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setTouristId(Long touristId) { this.touristId = touristId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }
    public void setToken(String token) { this.token = token; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setStatus(TourPurchaseTokenStatus status) { this.status = status; }
}