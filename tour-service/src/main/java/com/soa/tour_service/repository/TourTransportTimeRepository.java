package com.soa.tour_service.repository;

import com.soa.tour_service.model.TourTransportTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourTransportTimeRepository extends JpaRepository<TourTransportTime, Long> {
}