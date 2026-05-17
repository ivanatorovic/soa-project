package com.example.purchase_service.messaging;

import com.example.purchase_service.config.RabbitMQConfig;
import com.example.purchase_service.events.*;
import com.example.purchase_service.model.TourPurchaseToken;
import com.example.purchase_service.model.TourPurchaseTokenStatus;
import com.example.purchase_service.repository.TourPurchaseTokenRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TourExecutionPurchaseSagaListener {

    private final TourPurchaseTokenRepository tokenRepository;
    private final RabbitTemplate rabbitTemplate;

    public TourExecutionPurchaseSagaListener(
            TourPurchaseTokenRepository tokenRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.tokenRepository = tokenRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.TOUR_EXECUTION_START_REQUESTED_QUEUE)
    @Transactional
    public void handleStartRequested(StartTourExecutionRequestedEvent event) {
        try {
            TourPurchaseToken token = tokenRepository
                    .findByTouristIdAndTourId(event.getTouristId(), event.getTourId())
                    .orElseThrow(() -> new RuntimeException("Morate kupiti turu pre pokretanja"));

            if (token.getStatus() != TourPurchaseTokenStatus.AVAILABLE) {
                throw new RuntimeException("Token nije dostupan za pokretanje ture");
            }

            token.setStatus(TourPurchaseTokenStatus.IN_USE);
            tokenRepository.save(token);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TOURS_EXCHANGE,
                    RabbitMQConfig.TOUR_EXECUTION_TOKEN_RESERVED_ROUTING_KEY,
                    new TourExecutionTokenReservedEvent(
                            event.getTouristId(),
                            event.getTourId(),
                            event.getLatitude(),
                            event.getLongitude()
                    )
            );

        } catch (Exception e) {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TOURS_EXCHANGE,
                    RabbitMQConfig.TOUR_EXECUTION_START_FAILED_ROUTING_KEY,
                    new TourExecutionStartFailedEvent(
                            event.getTouristId(),
                            event.getTourId(),
                            e.getMessage()
                    )
            );
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TOUR_EXECUTION_START_FAILED_QUEUE)
    @Transactional
    public void handleStartFailed(TourExecutionStartFailedEvent event) {
        tokenRepository.findByTouristIdAndTourId(event.getTouristId(), event.getTourId())
                .ifPresent(token -> {
                    if (token.getStatus() == TourPurchaseTokenStatus.IN_USE) {
                        token.setStatus(TourPurchaseTokenStatus.AVAILABLE);
                        tokenRepository.save(token);
                    }
                });
    }

    @RabbitListener(queues = RabbitMQConfig.TOUR_EXECUTION_COMPLETED_QUEUE)
    @Transactional
    public void handleCompleted(TourExecutionCompletedEvent event) {
        tokenRepository.findByTouristIdAndTourId(event.getTouristId(), event.getTourId())
                .ifPresent(token -> {
                    token.setStatus(TourPurchaseTokenStatus.COMPLETED);
                    tokenRepository.save(token);
                });
    }

    @RabbitListener(queues = RabbitMQConfig.TOUR_EXECUTION_ABANDONED_QUEUE)
    @Transactional
    public void handleAbandoned(TourExecutionAbandonedEvent event) {
        tokenRepository.findByTouristIdAndTourId(event.getTouristId(), event.getTourId())
                .ifPresent(token -> {
                    token.setStatus(TourPurchaseTokenStatus.AVAILABLE);
                    tokenRepository.save(token);
                });
    }
}