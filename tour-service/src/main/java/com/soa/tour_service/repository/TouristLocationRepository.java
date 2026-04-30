package com.soa.tour_service.repository;

import com.soa.tour_service.model.TouristLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TouristLocationRepository extends JpaRepository<TouristLocation, Long> {

    Optional<TouristLocation> findByTouristId(Long touristId);
}