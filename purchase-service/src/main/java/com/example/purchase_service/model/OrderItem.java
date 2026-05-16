package com.example.purchase_service.model;




import jakarta.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tourId;

    private String tourName;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "shopping_cart_id")
    private ShoppingCart shoppingCart;

    public OrderItem() {
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

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
}