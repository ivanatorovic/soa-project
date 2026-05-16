package com.soa.tour_service.repository;

import com.soa.tour_service.model.TourExecution;
import com.soa.tour_service.model.TourExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TourExecutionRepository extends JpaRepository<TourExecution, Long> {

    Optional<TourExecution> findByTouristIdAndStatus(Long touristId, TourExecutionStatus status);

    Optional<TourExecution> findByIdAndTouristId(Long id, Long touristId);

    boolean existsByTouristIdAndTourIdAndStatusIn(
            Long touristId,
            Long tourId,
            List<TourExecutionStatus> statuses
    );

    List<TourExecution> findByTouristIdAndStatusIn(
            Long touristId,
            List<TourExecutionStatus> statuses
    );

}