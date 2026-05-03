package com.soa.tour_service.dto;

public class KeyPointResponse {

    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String imageUrl;

    public KeyPointResponse() {
    }

    public KeyPointResponse(Long id, String name, String description,
                            Double latitude, Double longitude, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
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

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}