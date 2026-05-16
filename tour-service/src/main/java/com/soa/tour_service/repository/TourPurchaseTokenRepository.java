package com.soa.tour_service.repository;

import com.soa.tour_service.model.TourPurchaseToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourPurchaseTokenRepository extends JpaRepository<TourPurchaseToken, Long> {

    boolean existsByTouristIdAndTourId(Long touristId, Long tourId);
    List<TourPurchaseToken> findByTouristId(Long touristId);
}