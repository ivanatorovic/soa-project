package com.soa.tour_service.repository;

import com.soa.tour_service.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTourId(Long tourId);

    long countByTourId(Long tourId);

    List<Review> findByTouristId(Long touristId);
}