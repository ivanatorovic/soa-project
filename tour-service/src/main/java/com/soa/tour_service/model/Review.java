package com.soa.tour_service.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, length = 2000)
    private String comment;

    @Column(nullable = false)
    private Long touristId;

    @Column
    private String touristUsername;

    @Column(nullable = false)
    private LocalDate visitedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(
            name = "review_images",
            joinColumns = @JoinColumn(name = "review_id")
    )
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    public Review() {
    }

    public Long getId() {
        return id;
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

    public Tour getTour() {
        return tour;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTouristId(Long touristId) {
        this.touristId = touristId;
    }

    public void setTouristUsername(String touristUsername) {
        this.touristUsername = touristUsername;
    }

    public void setVisitedAt(LocalDate visitedAt) {
        this.visitedAt = visitedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }
}