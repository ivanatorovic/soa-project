package com.soa.tour_service.repository;

import com.soa.tour_service.model.CompletedKeyPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedKeyPointRepository extends JpaRepository<CompletedKeyPoint, Long> {

    boolean existsByTourExecutionIdAndKeyPointId(Long executionId, Long keyPointId);
}