package com.soa.tour_service.dto;

import com.soa.tour_service.model.TourDifficulty;
import com.soa.tour_service.model.TourStatus;
import java.time.LocalDateTime;
import java.util.List;

public class TourResponse {

    private Long id;
    private String name;
    private String description;
    private TourDifficulty difficulty;
    private Double price;
    private TourStatus status;
    private Long authorId;
    private List<String> tags;
    private List<KeyPointResponse> keyPoints;
    private LocalDateTime publishedAt;
    private LocalDateTime archivedAt;
    private Double distanceInKm;
    private boolean inShoppingCart;
    private boolean purchased;
    private List<TourTransportTimeResponse> transportTimes;
    private Integer availableSlots;
    public TourResponse() {
    }

    public TourResponse(Long id, String name, String description,
                        TourDifficulty difficulty, Double price,
                        TourStatus status, Long authorId,
                        List<String> tags, List<KeyPointResponse> keyPoints) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.price = price;
        this.status = status;
        this.authorId = authorId;
        this.tags = tags;
        this.keyPoints = keyPoints;
    }

    public Integer getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(Integer availableSlots) {
        this.availableSlots = availableSlots;
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
    public boolean isInShoppingCart() {
        return inShoppingCart;
    }

    public void setInShoppingCart(boolean inShoppingCart) {
        this.inShoppingCart = inShoppingCart;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
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

    public List<String> getTags() {
        return tags;
    }

    public List<KeyPointResponse> getKeyPoints() {
        return keyPoints;
    }
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public Double getDistanceInKm() {
        return distanceInKm;
    }
    public List<TourTransportTimeResponse> getTransportTimes() {
        return transportTimes;
    }
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public void setDistanceInKm(Double distanceInKm) {
        this.distanceInKm = distanceInKm;
    }
    public void setTransportTimes(List<TourTransportTimeResponse> transportTimes) {
        this.transportTimes = transportTimes;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setKeyPoints(List<KeyPointResponse> keyPoints) {
        this.keyPoints = keyPoints;
    }
}