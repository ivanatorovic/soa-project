package com.example.purchase_service.repository;

import com.example.purchase_service.model.TourPurchaseToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TourPurchaseTokenRepository extends JpaRepository<TourPurchaseToken, Long> {

    boolean existsByTouristIdAndTourId(Long touristId, Long tourId);

    List<TourPurchaseToken> findByTouristId(Long touristId);

    Optional<TourPurchaseToken> findByTouristIdAndTourId(Long touristId, Long tourId);

    void deleteByTouristIdAndTourId(Long touristId, Long tourId);
}