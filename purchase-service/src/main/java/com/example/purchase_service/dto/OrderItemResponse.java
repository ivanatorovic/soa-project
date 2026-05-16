package com.example.purchase_service.dto;



public class OrderItemResponse {

    private Long id;
    private Long tourId;
    private String tourName;
    private Double price;

    public OrderItemResponse(Long id, Long tourId, String tourName, Double price) {
        this.id = id;
        this.tourId = tourId;
        this.tourName = tourName;
        this.price = price;
    }

    public Long getId() {
        return id;
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
