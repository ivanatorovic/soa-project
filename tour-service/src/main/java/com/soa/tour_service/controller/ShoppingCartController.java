package com.soa.tour_service.controller;

import com.soa.tour_service.dto.ShoppingCartResponse;
import com.soa.tour_service.dto.TourResponse;
import com.soa.tour_service.security.AuthenticatedUser;
import com.soa.tour_service.service.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tours/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    public ShoppingCartResponse getCart(Authentication authentication) {

        AuthenticatedUser user =
                (AuthenticatedUser) authentication.getPrincipal();

        Long touristId = user.getId();

        return shoppingCartService.getCart(touristId);
    }

    @PostMapping("/items/{tourId}")
    public ShoppingCartResponse addTourToCart(
            @PathVariable Long tourId,
            Authentication authentication
    ) {

        AuthenticatedUser user =
                (AuthenticatedUser) authentication.getPrincipal();

        Long touristId = user.getId();

        return shoppingCartService.addTourToCart(touristId, tourId);
    }

    @DeleteMapping("/items/{itemId}")
    public ShoppingCartResponse removeItemFromCart(
            @PathVariable Long itemId,
            Authentication authentication
    ) {

        AuthenticatedUser user =
                (AuthenticatedUser) authentication.getPrincipal();

        Long touristId = user.getId();

        return shoppingCartService.removeItemFromCart(touristId, itemId);
    }

    @PostMapping("/checkout")
    public ShoppingCartResponse checkout(Authentication authentication) {

        AuthenticatedUser user =
                (AuthenticatedUser) authentication.getPrincipal();

        Long touristId = user.getId();

        return shoppingCartService.checkout(touristId);
    }


}