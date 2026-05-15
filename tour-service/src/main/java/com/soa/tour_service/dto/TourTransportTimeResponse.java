package com.soa.tour_service.dto;

import com.soa.tour_service.model.TransportType;

public class TourTransportTimeResponse {

    private Long id;
    private TransportType transportType;
    private Integer durationMinutes;

    public TourTransportTimeResponse() {
    }

    public TourTransportTimeResponse(Long id, TransportType transportType, Integer durationMinutes) {
        this.id = id;
        this.transportType = transportType;
        this.durationMinutes = durationMinutes;
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
}