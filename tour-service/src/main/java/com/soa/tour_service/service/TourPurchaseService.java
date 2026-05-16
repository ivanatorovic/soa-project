package com.soa.tour_service.service;

import com.soa.tour_service.dto.TourResponse;
import com.soa.tour_service.dto.TourTransportTimeResponse;
import com.soa.tour_service.model.Tour;
import com.soa.tour_service.model.TourPurchaseToken;
import com.soa.tour_service.repository.TourPurchaseTokenRepository;
import com.soa.tour_service.repository.TourRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TourPurchaseService {

    private final TourPurchaseTokenRepository tokenRepository;
    private final TourRepository tourRepository;

    public TourPurchaseService(
            TourPurchaseTokenRepository tokenRepository,
            TourRepository tourRepository
    ) {
        this.tokenRepository = tokenRepository;
        this.tourRepository = tourRepository;
    }

    public List<TourResponse> getPurchasedTours(Long touristId) {
        return tokenRepository.findByTouristId(touristId)
                .stream()
                .map(token -> tourRepository.findById(token.getTourId())
                        .orElse(null))
                .filter(Objects::nonNull)
                .map(this::mapToTourResponse)
                .toList();
    }

    private TourResponse mapToTourResponse(Tour tour) {

        TourResponse response = new TourResponse();

        response.setId(tour.getId());
        response.setName(tour.getName());
        response.setDescription(tour.getDescription());
        response.setDifficulty(tour.getDifficulty());
        response.setPrice(tour.getPrice());
        response.setStatus(tour.getStatus());
        response.setAuthorId(tour.getAuthorId());

        response.setDistanceInKm(tour.getDistanceInKm());
        response.setPublishedAt(tour.getPublishedAt());
        response.setArchivedAt(tour.getArchivedAt());

        response.setTransportTimes(
                tour.getTransportTimes()
                        .stream()
                        .map(tt -> new TourTransportTimeResponse(
                                tt.getId(),
                                tt.getTransportType(),
                                tt.getDurationMinutes()
                        ))
                        .toList()
        );

        return response;
    }
}