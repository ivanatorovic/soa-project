package com.example.purchase_service.model;


import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long touristId;

    private Double totalPrice = 0.0;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public ShoppingCart() {
    }

    public void recalculateTotalPrice() {
        this.totalPrice = items.stream()
                .mapToDouble(OrderItem::getPrice)
                .sum();
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTouristId(Long touristId) {
        this.touristId = touristId;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
