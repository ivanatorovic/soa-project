package com.example.purchase_service.events;

public class TourExecutionCompletedEvent {

    private Long touristId;
    private Long tourId;

    public TourExecutionCompletedEvent() {
    }

    public TourExecutionCompletedEvent(Long touristId, Long tourId) {
        this.touristId = touristId;
        this.tourId = tourId;
    }

    public Long getTouristId() {
        return touristId;
    }

    public void setTouristId(Long touristId) {
        this.touristId = touristId;
    }

    public Long getTourId() {
        return tourId;
    }

    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }
}