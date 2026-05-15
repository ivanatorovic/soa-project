package com.soa.tour_service.model;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tour")
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourDifficulty difficulty;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TourStatus status;

    private LocalDateTime publishedAt;

    private LocalDateTime archivedAt;

    private Double distanceInKm = 0.0;

    @Column(nullable = false)
    private Long authorId;

    @ManyToMany
    @JoinTable(
            name = "tour_tag",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL)
    private List<KeyPoint> keyPoints = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourTransportTime> transportTimes = new ArrayList<>();
    public Tour() {
    }

    public void setKeyPoints(List<KeyPoint> keyPoints) {
        this.keyPoints = keyPoints;
    }

    public List<KeyPoint> getKeyPoints() {
        return keyPoints;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TourDifficulty getDifficulty() {
        return difficulty;
    }

    public Double getPrice() {
        return price;
    }

    public TourStatus getStatus() {
        return status;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(TourDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setStatus(TourStatus status) {
        this.status = status;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public Double getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(Double distanceInKm) {
        this.distanceInKm = distanceInKm;
    }
    public List<TourTransportTime> getTransportTimes() {
        return transportTimes;
    }

    public void setTransportTimes(List<TourTransportTime> transportTimes) {
        this.transportTimes = transportTimes;
    }
}