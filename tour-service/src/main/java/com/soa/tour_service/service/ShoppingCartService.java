package com.soa.tour_service.service;

import com.soa.tour_service.dto.OrderItemResponse;
import com.soa.tour_service.dto.ShoppingCartResponse;
import com.soa.tour_service.dto.TourResponse;
import com.soa.tour_service.model.*;
import com.soa.tour_service.repository.OrderItemRepository;
import com.soa.tour_service.repository.ShoppingCartRepository;
import com.soa.tour_service.repository.TourPurchaseTokenRepository;
import com.soa.tour_service.repository.TourRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final TourRepository tourRepository;
    private final TourPurchaseTokenRepository tokenRepository;

    public ShoppingCartService(
            ShoppingCartRepository shoppingCartRepository,
            OrderItemRepository orderItemRepository,
            TourRepository tourRepository,
            TourPurchaseTokenRepository tokenRepository
    ) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.orderItemRepository = orderItemRepository;
        this.tourRepository = tourRepository;
        this.tokenRepository = tokenRepository;
    }

    public ShoppingCartResponse getCart(Long touristId) {
        ShoppingCart cart = getOrCreateCart(touristId);
        return mapToResponse(cart);
    }

    public ShoppingCartResponse addTourToCart(Long touristId, Long tourId) {
        ShoppingCart cart = getOrCreateCart(touristId);

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tura nije pronađena"));

        if (tour.getStatus() != TourStatus.PUBLISHED) {
            throw new RuntimeException("Samo objavljene ture mogu da se kupe");
        }

        boolean alreadyInCart = cart.getItems()
                .stream()
                .anyMatch(item -> item.getTourId().equals(tourId));

        if (alreadyInCart) {
            throw new RuntimeException("Tura je već u korpi");
        }

        boolean alreadyPurchased = tokenRepository.existsByTouristIdAndTourId(touristId, tourId);

        if (alreadyPurchased) {
            throw new RuntimeException("Tura je već kupljena");
        }

        OrderItem item = new OrderItem();
        item.setTourId(tour.getId());
        item.setTourName(tour.getName());
        item.setPrice(tour.getPrice());
        item.setShoppingCart(cart);

        cart.getItems().add(item);
        cart.recalculateTotalPrice();

        shoppingCartRepository.save(cart);

        return mapToResponse(cart);
    }

    public ShoppingCartResponse removeItemFromCart(Long touristId, Long itemId) {
        ShoppingCart cart = getOrCreateCart(touristId);

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Stavka nije pronađena"));

        if (!item.getShoppingCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Stavka ne pripada vašoj korpi");
        }

        cart.getItems().remove(item);
        cart.recalculateTotalPrice();

        shoppingCartRepository.save(cart);

        return mapToResponse(cart);
    }

    public ShoppingCartResponse checkout(Long touristId) {
        ShoppingCart cart = getOrCreateCart(touristId);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Korpa je prazna");
        }

        for (OrderItem item : cart.getItems()) {
            boolean alreadyPurchased = tokenRepository.existsByTouristIdAndTourId(touristId, item.getTourId());

            if (!alreadyPurchased) {
                TourPurchaseToken token = new TourPurchaseToken();
                token.setTouristId(touristId);
                token.setTourId(item.getTourId());
                token.setToken(UUID.randomUUID().toString());
                token.setCreatedAt(LocalDateTime.now());

                tokenRepository.save(token);
            }
        }

        cart.getItems().clear();
        cart.setTotalPrice(0.0);

        shoppingCartRepository.save(cart);

        return mapToResponse(cart);
    }

    private ShoppingCart getOrCreateCart(Long touristId) {
        return shoppingCartRepository.findByTouristId(touristId)
                .orElseGet(() -> {
                    ShoppingCart cart = new ShoppingCart();
                    cart.setTouristId(touristId);
                    cart.setTotalPrice(0.0);
                    return shoppingCartRepository.save(cart);
                });
    }

    private ShoppingCartResponse mapToResponse(ShoppingCart cart) {
        return new ShoppingCartResponse(
                cart.getId(),
                cart.getTouristId(),
                cart.getTotalPrice(),
                cart.getItems()
                        .stream()
                        .map(item -> new OrderItemResponse(
                                item.getId(),
                                item.getTourId(),
                                item.getTourName(),
                                item.getPrice()
                        ))
                        .collect(Collectors.toList())
        );
    }


}