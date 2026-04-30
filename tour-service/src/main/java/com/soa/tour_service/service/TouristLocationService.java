package com.soa.tour_service.service;

import com.soa.tour_service.dto.TouristLocationResponse;
import com.soa.tour_service.dto.UpdateTouristLocationRequest;
import com.soa.tour_service.model.TouristLocation;
import com.soa.tour_service.repository.TouristLocationRepository;
import org.springframework.stereotype.Service;

@Service
public class TouristLocationService {

    private final TouristLocationRepository touristLocationRepository;

    public TouristLocationService(TouristLocationRepository touristLocationRepository) {
        this.touristLocationRepository = touristLocationRepository;
    }

    public TouristLocationResponse getCurrentLocation(Long touristId) {
        TouristLocation location = touristLocationRepository.findByTouristId(touristId)
                .orElse(null);

        if (location == null) {
            return null;
        }

        return new TouristLocationResponse(
                location.getLatitude(),
                location.getLongitude()
        );
    }

    public TouristLocationResponse updateCurrentLocation(
            Long touristId,
            UpdateTouristLocationRequest request
    ) {
        TouristLocation location = touristLocationRepository.findByTouristId(touristId)
                .orElse(new TouristLocation());

        location.setTouristId(touristId);
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());

        TouristLocation saved = touristLocationRepository.save(location);

        return new TouristLocationResponse(
                saved.getLatitude(),
                saved.getLongitude()
        );
    }
}