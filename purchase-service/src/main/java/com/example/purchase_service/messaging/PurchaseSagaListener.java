package com.example.purchase_service.messaging;

import com.example.purchase_service.config.RabbitMQConfig;
import com.example.purchase_service.events.PurchaseTourItem;
import com.example.purchase_service.events.ToursReservationCancelEvent;
import com.example.purchase_service.events.ToursReservationFailedEvent;
import com.example.purchase_service.events.ToursReservedEvent;
import com.example.purchase_service.model.*;
import com.example.purchase_service.repository.PurchaseRepository;
import com.example.purchase_service.repository.ShoppingCartRepository;
import com.example.purchase_service.repository.TourPurchaseTokenRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PurchaseSagaListener {

    private final TourPurchaseTokenRepository tokenRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PurchaseRepository purchaseRepository;

    @Value("${app.test.fail-token-creation:false}")
    private boolean failTokenCreation;

    public PurchaseSagaListener(
            TourPurchaseTokenRepository tokenRepository,
            ShoppingCartRepository shoppingCartRepository,
            RabbitTemplate rabbitTemplate,
            PurchaseRepository purchaseRepository
    ) {
        this.tokenRepository = tokenRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.purchaseRepository = purchaseRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.TOURS_RESERVED_QUEUE)
    @Transactional
    public void handleToursReserved(ToursReservedEvent event) {
        try {
            int createdTokens = 0;

            for (PurchaseTourItem item : event.getItems()) {
                boolean alreadyPurchased =
                        tokenRepository.existsByTouristIdAndTourId(event.getTouristId(), item.getTourId());

                if (!alreadyPurchased) {
                    TourPurchaseToken token = new TourPurchaseToken();
                    token.setTouristId(event.getTouristId());
                    token.setTourId(item.getTourId());
                    token.setToken(UUID.randomUUID().toString());
                    token.setCreatedAt(LocalDateTime.now());
                    token.setStatus(TourPurchaseTokenStatus.AVAILABLE);

                    tokenRepository.save(token);
                    createdTokens++;
                }

                if (failTokenCreation && createdTokens == 1) {
                    throw new RuntimeException("TEST: Puklo kreiranje tokena");
                }
            }

            ShoppingCart cart = shoppingCartRepository.findByTouristId(event.getTouristId())
                    .orElseThrow(() -> new RuntimeException("Korpa nije pronađena"));

            cart.getItems().clear();
            cart.setTotalPrice(0.0);
            shoppingCartRepository.save(cart);

            Purchase purchase = purchaseRepository.findByPurchaseId(event.getPurchaseId())
                    .orElseThrow(() -> new RuntimeException("Kupovina nije pronađena"));

            purchase.setStatus(PurchaseStatus.COMPLETED);
            purchase.setCompletedAt(LocalDateTime.now());
            purchaseRepository.save(purchase);

        } catch (Exception e) {
            for (PurchaseTourItem item : event.getItems()) {
                tokenRepository.deleteByTouristIdAndTourId(event.getTouristId(), item.getTourId());
            }

            purchaseRepository.findByPurchaseId(event.getPurchaseId()).ifPresent(purchase -> {
                purchase.setStatus(PurchaseStatus.FAILED);
                purchase.setFailureReason("Greška pri kreiranju tokena: " + e.getMessage());
                purchase.setCompletedAt(LocalDateTime.now());
                purchaseRepository.save(purchase);
            });

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TOURS_EXCHANGE,
                    RabbitMQConfig.TOURS_RESERVATION_CANCEL_ROUTING_KEY,
                    new ToursReservationCancelEvent(
                            event.getPurchaseId(),
                            event.getTouristId(),
                            event.getItems(),
                            "Purchase-service failed: " + e.getMessage()
                    )
            );
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TOURS_RESERVATION_FAILED_QUEUE)
    public void handleToursReservationFailed(ToursReservationFailedEvent event) {
        purchaseRepository.findByPurchaseId(event.getPurchaseId()).ifPresent(purchase -> {
            purchase.setStatus(PurchaseStatus.FAILED);
            purchase.setFailureReason(event.getReason());
            purchase.setCompletedAt(LocalDateTime.now());
            purchaseRepository.save(purchase);
        });

        System.out.println("Kupovina nije uspela: " + event.getReason());
    }
}