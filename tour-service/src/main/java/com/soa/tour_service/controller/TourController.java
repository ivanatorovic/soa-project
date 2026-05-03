package com.soa.tour_service.controller;

import com.soa.tour_service.dto.CreateKeyPointRequest;
import com.soa.tour_service.dto.CreateTourRequest;
import com.soa.tour_service.dto.TourResponse;
import com.soa.tour_service.security.AuthenticatedUser;
import com.soa.tour_service.service.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

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


    @PostMapping(value = "/{tourId}/key-points", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> addKeyPoint(
            @PathVariable Long tourId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        tourService.addKeyPoint(tourId, name, description, latitude, longitude, image, user.getId());
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

    @PutMapping(value = "/{tourId}/key-points/{keyPointId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateKeyPoint(
            @PathVariable Long tourId,
            @PathVariable Long keyPointId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        tourService.updateKeyPoint(tourId, keyPointId, name, description, latitude, longitude, image, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{tourId}/key-points/{keyPointId}")
    public ResponseEntity<Void> deleteKeyPoint(
            @PathVariable Long tourId,
            @PathVariable Long keyPointId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        tourService.deleteKeyPoint(tourId, keyPointId, user.getId());
        return ResponseEntity.ok().build();
    }
}