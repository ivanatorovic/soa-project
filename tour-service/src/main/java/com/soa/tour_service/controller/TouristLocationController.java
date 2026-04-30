package com.soa.tour_service.controller;

import com.soa.tour_service.dto.TouristLocationResponse;
import com.soa.tour_service.dto.UpdateTouristLocationRequest;
import com.soa.tour_service.security.AuthenticatedUser;
import com.soa.tour_service.service.TouristLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tours/tourist-location")
public class TouristLocationController {

    private final TouristLocationService touristLocationService;

    public TouristLocationController(TouristLocationService touristLocationService) {
        this.touristLocationService = touristLocationService;
    }

    @GetMapping
    public ResponseEntity<TouristLocationResponse> getCurrentLocation(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        TouristLocationResponse response =
                touristLocationService.getCurrentLocation(user.getId());

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<TouristLocationResponse> updateCurrentLocation(
            @RequestBody UpdateTouristLocationRequest request,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        TouristLocationResponse response =
                touristLocationService.updateCurrentLocation(user.getId(), request);

        return ResponseEntity.ok(response);
    }
}