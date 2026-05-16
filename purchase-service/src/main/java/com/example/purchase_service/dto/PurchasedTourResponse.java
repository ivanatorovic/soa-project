package com.example.purchase_service.dto;

public class PurchasedTourResponse {

    private Long tourId;
    private String tourName;
    private Double price;

    public PurchasedTourResponse(Long tourId, String tourName, Double price) {
        this.tourId = tourId;
        this.tourName = tourName;
        this.price = price;
    }

    public Long getTourId() {
        return tourId;
    }

    public String getTourName() {
        return tourName;
    }

    public Double getPrice() {
        return price;
    }
}