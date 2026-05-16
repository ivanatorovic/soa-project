package com.soa.tour_service.controller;

import com.soa.tour_service.dto.TourResponse;
import com.soa.tour_service.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.soa.tour_service.service.TourPurchaseService;

import java.util.List;

@RestController
@RequestMapping("/api/tours/purchases")
public class TourPurchaseController {

    private final TourPurchaseService tourPurchaseService;

    public TourPurchaseController(TourPurchaseService tourPurchaseService) {
        this.tourPurchaseService = tourPurchaseService;
    }

    @GetMapping
    public ResponseEntity<List<TourResponse>> getPurchasedTours(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(
                tourPurchaseService.getPurchasedTours(user.getId())
        );
    }
}
