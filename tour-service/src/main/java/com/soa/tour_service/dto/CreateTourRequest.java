package com.soa.tour_service.dto;



import com.soa.tour_service.model.TourDifficulty;
import java.util.List;

public class CreateTourRequest {
    private String name;
    private String description;
    private TourDifficulty difficulty;
    private Integer availableSlots;
    private List<String> tags;

    public CreateTourRequest() {
    }

    public Integer getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(Integer availableSlots) {
        this.availableSlots = availableSlots;
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

    public List<String> getTags() {
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

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
