package com.soa.tour_service.controller;

import com.soa.tour_service.dto.CheckKeyPointRequest;
import com.soa.tour_service.dto.StartTourExecutionRequest;
import com.soa.tour_service.dto.TourExecutionResponse;
import com.soa.tour_service.security.AuthenticatedUser;
import com.soa.tour_service.service.TourExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tours/executions")
public class TourExecutionController {

    private final TourExecutionService tourExecutionService;

    public TourExecutionController(TourExecutionService tourExecutionService) {
        this.tourExecutionService = tourExecutionService;
    }

    @PostMapping("/start/{tourId}")
    public ResponseEntity<Map<String, String>> startTour(
            @PathVariable Long tourId,
            @RequestBody StartTourExecutionRequest request,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        String message = tourExecutionService.startTour(user.getId(), tourId, request);

        return ResponseEntity.ok(
                Map.of("message", message)
        );
    }

    @GetMapping("/active")
    public ResponseEntity<TourExecutionResponse> getActiveTour(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        TourExecutionResponse response = tourExecutionService.getActiveTour(user.getId());

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{executionId}/check-key-points")
    public ResponseEntity<TourExecutionResponse> checkKeyPoints(
            @PathVariable Long executionId,
            @RequestBody CheckKeyPointRequest request,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                tourExecutionService.checkKeyPoints(user.getId(), executionId, request)
        );
    }

    @PostMapping("/{executionId}/complete")
    public ResponseEntity<TourExecutionResponse> completeTour(
            @PathVariable Long executionId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                tourExecutionService.completeTour(user.getId(), executionId)
        );
    }

    @PostMapping("/{executionId}/abandon")
    public ResponseEntity<TourExecutionResponse> abandonTour(
            @PathVariable Long executionId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                tourExecutionService.abandonTour(user.getId(), executionId)
        );
    }

    @GetMapping("/completed")
    public ResponseEntity<List<TourExecutionResponse>> getCompletedExecutions(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                tourExecutionService.getCompletedExecutions(user.getId())
        );
    }


}