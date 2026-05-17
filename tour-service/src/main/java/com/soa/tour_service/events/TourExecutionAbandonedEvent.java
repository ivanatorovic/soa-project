package com.soa.tour_service.events;

public class TourExecutionAbandonedEvent {

    private Long touristId;
    private Long tourId;

    public TourExecutionAbandonedEvent() {
    }

    public TourExecutionAbandonedEvent(Long touristId, Long tourId) {
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