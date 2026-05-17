package com.soa.tour_service.events;

public class PurchaseTourItem {
    private Long tourId;

    public PurchaseTourItem() {}

    public PurchaseTourItem(Long tourId) {
        this.tourId = tourId;
    }

    public Long getTourId() {
        return tourId;
    }

    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }
}
