package com.example.purchase_service.events;

public class TourExecutionStartFailedEvent {
    private Long touristId;
    private Long tourId;
    private String reason;

    public TourExecutionStartFailedEvent() {}

    public TourExecutionStartFailedEvent(Long touristId, Long tourId, String reason) {
        this.touristId = touristId;
        this.tourId = tourId;
        this.reason = reason;
    }

    public Long getTouristId() { return touristId; }
    public Long getTourId() { return tourId; }
    public String getReason() { return reason; }

    public void setTouristId(Long touristId) { this.touristId = touristId; }
    public void setTourId(Long tourId) { this.tourId = tourId; }
    public void setReason(String reason) { this.reason = reason; }
}