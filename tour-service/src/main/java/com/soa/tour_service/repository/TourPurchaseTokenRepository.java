package com.soa.tour_service.repository;

import com.soa.tour_service.model.TourPurchaseToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourPurchaseTokenRepository extends JpaRepository<TourPurchaseToken, Long> {

    boolean existsByTouristIdAndTourId(Long touristId, Long tourId);
}