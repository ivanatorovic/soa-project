package com.soa.tour_service.repository;

import com.soa.tour_service.model.KeyPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyPointRepository extends JpaRepository<KeyPoint, Long> {
}