package com.soa.tour_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tourist_location")
public class TouristLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long touristId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    public TouristLocation() {
    }

    public Long getId() {
        return id;
    }

    public Long getTouristId() {
        return touristId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setTouristId(Long touristId) {
        this.touristId = touristId;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}