package com.example.purchase_service.dto;


import java.util.List;

public class ShoppingCartResponse {

    private Long id;
    private Long touristId;
    private Double totalPrice;
    private List<OrderItemResponse> items;

    public ShoppingCartResponse(Long id, Long touristId, Double totalPrice, List<OrderItemResponse> items) {
        this.id = id;
        this.touristId = touristId;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public Long getTouristId() {
        return touristId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}