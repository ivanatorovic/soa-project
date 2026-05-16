package com.example.purchase_service.controller;

import com.example.purchase_service.dto.ShoppingCartResponse;
import com.example.purchase_service.security.AuthenticatedUser;
import com.example.purchase_service.service.ShoppingCartService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    public ShoppingCartResponse getCart(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return shoppingCartService.getCart(user.getId());
    }

    @PostMapping("/items/{tourId}")
    public ShoppingCartResponse addTourToCart(
            @PathVariable Long tourId,
            Authentication authentication,
            @RequestHeader("Authorization") String authHeader
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        String jwt = authHeader.replace("Bearer ", "");

        return shoppingCartService.addTourToCart(user.getId(), tourId, jwt);
    }

    @DeleteMapping("/items/{itemId}")
    public ShoppingCartResponse removeItemFromCart(
            @PathVariable Long itemId,
            Authentication authentication
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return shoppingCartService.removeItemFromCart(user.getId(), itemId);
    }

    @PostMapping("/checkout")
    public ShoppingCartResponse checkout(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return shoppingCartService.checkout(user.getId());
    }


    @GetMapping("/tokens/exists")
    public boolean hasPurchasedTour(
            @RequestParam Long touristId,
            @RequestParam Long tourId
    ) {
        return shoppingCartService.hasPurchasedTour(touristId, tourId);
    }

    @GetMapping("/contains")
    public boolean cartContainsTour(
            @RequestParam Long touristId,
            @RequestParam Long tourId
    ) {
        return shoppingCartService.cartContainsTour(touristId, tourId);
    }
}