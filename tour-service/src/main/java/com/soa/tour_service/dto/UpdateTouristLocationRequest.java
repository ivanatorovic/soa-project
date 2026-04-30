package com.soa.tour_service.dto;

public class UpdateTouristLocationRequest {

    private Double latitude;
    private Double longitude;

    public UpdateTouristLocationRequest() {
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}