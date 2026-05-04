package com.soa.tour_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponse {

    private Long id;
    private Long tourId;
    private Integer rating;
    private String comment;
    private Long touristId;
    private String touristUsername;
    private LocalDate visitedAt;
    private LocalDateTime createdAt;
    private List<String> imageUrls;

    public ReviewResponse() {
    }

    public ReviewResponse(
            Long id,
            Long tourId,
            Integer rating,
            String comment,
            Long touristId,
            String touristUsername,
            LocalDate visitedAt,
            LocalDateTime createdAt,
            List<String> imageUrls
    ) {
        this.id = id;
        this.tourId = tourId;
        this.rating = rating;
        this.comment = comment;
        this.touristId = touristId;
        this.touristUsername = touristUsername;
        this.visitedAt = visitedAt;
        this.createdAt = createdAt;
        this.imageUrls = imageUrls;
    }

    public Long getId() {
        return id;
    }

    public Long getTourId() {
        return tourId;
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Long getTouristId() {
        return touristId;
    }

    public String getTouristUsername() {
        return touristUsername;
    }

    public LocalDate getVisitedAt() {
        return visitedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }
}