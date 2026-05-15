package com.soa.tour_service.repository;

import com.soa.tour_service.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.soa.tour_service.model.TourStatus;
public interface TourRepository extends JpaRepository<Tour, Long> {
    List<Tour> findByAuthorId(Long authorId);
    List<Tour> findByStatus(TourStatus status);
}