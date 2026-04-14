package com.soa.tour_service.controller;

import com.soa.tour_service.dto.CreateKeyPointRequest;
import com.soa.tour_service.dto.CreateTourRequest;
import com.soa.tour_service.dto.TourResponse;
import com.soa.tour_service.security.AuthenticatedUser;
import com.soa.tour_service.service.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping
    public ResponseEntity<TourResponse> createTour(
            @RequestBody CreateTourRequest request,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        TourResponse response = tourService.createTour(request, user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<TourResponse>> getMyTours(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(tourService.getMyTours(user.getId()));
    }


    @PostMapping("/{tourId}/key-points")
    public ResponseEntity<?> addKeyPoint(
            @PathVariable Long tourId,
            @RequestBody CreateKeyPointRequest request,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        tourService.addKeyPoint(tourId, request, user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<TourResponse> getTourById(@PathVariable Long tourId) {
        return ResponseEntity.ok(tourService.getTourById(tourId));
    }

    @GetMapping
    public ResponseEntity<List<TourResponse>> getAllTours() {
        return ResponseEntity.ok(tourService.getAllTours());
    }
}