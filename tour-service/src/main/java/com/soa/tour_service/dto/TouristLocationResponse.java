package com.soa.tour_service.dto;

public class TouristLocationResponse {

    private Double latitude;
    private Double longitude;

    public TouristLocationResponse() {
    }

    public TouristLocationResponse(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}