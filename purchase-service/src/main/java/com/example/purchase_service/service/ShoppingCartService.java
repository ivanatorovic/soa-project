package com.example.purchase_service.service;

import com.example.purchase_service.dto.OrderItemResponse;
import com.example.purchase_service.dto.PurchasedTourResponse;
import com.example.purchase_service.dto.ShoppingCartResponse;
import com.example.purchase_service.dto.TourResponse;
import com.example.purchase_service.model.OrderItem;
import com.example.purchase_service.model.ShoppingCart;
import com.example.purchase_service.model.TourPurchaseToken;
import com.example.purchase_service.repository.OrderItemRepository;
import com.example.purchase_service.repository.ShoppingCartRepository;
import com.example.purchase_service.repository.TourPurchaseTokenRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final TourPurchaseTokenRepository tokenRepository;
    private final RestTemplate restTemplate;

    public ShoppingCartService(
            ShoppingCartRepository shoppingCartRepository,
            OrderItemRepository orderItemRepository,
            TourPurchaseTokenRepository tokenRepository,
            RestTemplate restTemplate
    ) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.orderItemRepository = orderItemRepository;
        this.tokenRepository = tokenRepository;
        this.restTemplate = restTemplate;
    }

    public ShoppingCartResponse getCart(Long touristId) {
        ShoppingCart cart = getOrCreateCart(touristId);
        return mapToResponse(cart);
    }

    public ShoppingCartResponse addTourToCart(Long touristId, Long tourId, String jwt) {
        ShoppingCart cart = getOrCreateCart(touristId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<TourResponse> response = restTemplate.exchange(
                "http://tours-service:8083/api/tours/" + tourId,
                HttpMethod.GET,
                entity,
                TourResponse.class
        );

        TourResponse tour = response.getBody();

        if (tour == null) {
            throw new RuntimeException("Tura nije pronađena");
        }

        if (!"PUBLISHED".equals(tour.getStatus())) {
            throw new RuntimeException("Samo objavljene ture mogu da se kupe");
        }

        boolean alreadyInCart = cart.getItems()
                .stream()
                .anyMatch(item -> item.getTourId().equals(tourId));

        if (alreadyInCart) {
            throw new RuntimeException("Tura je već u korpi");
        }

        boolean alreadyPurchased =
                tokenRepository.existsByTouristIdAndTourId(touristId, tourId);

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
            boolean alreadyPurchased =
                    tokenRepository.existsByTouristIdAndTourId(touristId, item.getTourId());

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
    public boolean hasPurchasedTour(Long touristId, Long tourId) {
        return tokenRepository.existsByTouristIdAndTourId(touristId, tourId);
    }
    public boolean cartContainsTour(Long touristId, Long tourId) {

        return shoppingCartRepository.findByTouristId(touristId)
                .map(cart -> cart.getItems()
                        .stream()
                        .anyMatch(item -> item.getTourId().equals(tourId)))
                .orElse(false);
    }

    public List<TourResponse> getPurchasedTours(Long touristId, String jwt) {
        List<TourPurchaseToken> tokens = tokenRepository.findByTouristId(touristId);

        return tokens.stream()
                .map(token -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setBearerAuth(jwt);

                    HttpEntity<Void> entity = new HttpEntity<>(headers);

                    ResponseEntity<TourResponse> response = restTemplate.exchange(
                            "http://tours-service:8083/api/tours/" + token.getTourId(),
                            HttpMethod.GET,
                            entity,
                            TourResponse.class
                    );

                    return response.getBody();
                })
                .filter(tour -> tour != null)
                .toList();
    }
}