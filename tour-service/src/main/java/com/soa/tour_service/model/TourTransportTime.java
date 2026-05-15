package com.soa.tour_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tour_transport_time")
public class TourTransportTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType transportType;

    @Column(nullable = false)
    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    public TourTransportTime() {
    }

    public Long getId() {
        return id;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }
}