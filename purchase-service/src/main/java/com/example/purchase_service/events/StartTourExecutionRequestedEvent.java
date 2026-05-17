package com.example.purchase_service.events;

public class StartTourExecutionRequestedEvent {
    private Long touristId;
    private Long tourId;
    private Double latitude;
    private Double longitude;

    public StartTourExecutionRequestedEvent() {}

    public StartTourExecutionRequestedEvent(Long touristId, Long tourId, Double latitude, Double longitude) {
        this.touristId = touristId;
        this.tourId = tourId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getTouristId() { return touristId; }
    public Long getTourId() { return tourId; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

    public void setTouristId(Long touristId) { this.touristId = touristId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}