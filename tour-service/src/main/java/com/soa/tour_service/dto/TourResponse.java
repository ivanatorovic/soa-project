package com.soa.tour_service.dto;

import com.soa.tour_service.model.TourDifficulty;
import com.soa.tour_service.model.TourStatus;

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

    public List<String> getTags() {
        return tags;
    }

    public List<KeyPointResponse> getKeyPoints() {
        return keyPoints;
    }
}