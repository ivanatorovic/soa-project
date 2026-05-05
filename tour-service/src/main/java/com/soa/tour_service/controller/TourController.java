package com.soa.tour_service.controller;

import com.soa.tour_service.dto.CreateReviewRequest;
import com.soa.tour_service.dto.CreateTourRequest;
import com.soa.tour_service.dto.ReviewResponse;
import com.soa.tour_service.dto.TourResponse;
import com.soa.tour_service.security.AuthenticatedUser;
import com.soa.tour_service.service.ReviewService;
import com.soa.tour_service.service.TourService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private final TourService tourService;
    private final ReviewService reviewService;

    public TourController(TourService tourService, ReviewService reviewService) {
        this.tourService = tourService;
        this.reviewService = reviewService;
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

    @PostMapping(value = "/{tourId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable Long tourId,
            @RequestPart("info") String infoJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal AuthenticatedUser user,
            HttpServletRequest request
    ) {
        ReviewResponse response = reviewService.createReview(
                tourId,
                infoJson,
                images,
                user.getId(),
                user.getUsername(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tourId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviewsForTour(
            @PathVariable Long tourId
    ) {
        return ResponseEntity.ok(reviewService.getReviewsForTour(tourId));
    }

    @GetMapping("/{tourId}/reviews/count")
    public ResponseEntity<Long> countReviewsForTour(
            @PathVariable Long tourId
    ) {
        return ResponseEntity.ok(reviewService.countReviewsForTour(tourId));
    }
}