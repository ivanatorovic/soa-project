package com.soa.tour_service.messaging;

import com.soa.tour_service.config.RabbitMQConfig;
import com.soa.tour_service.events.TourExecutionStartFailedEvent;
import com.soa.tour_service.events.TourExecutionTokenReservedEvent;
import com.soa.tour_service.model.Tour;
import com.soa.tour_service.model.TourExecution;
import com.soa.tour_service.model.TourExecutionStatus;
import com.soa.tour_service.repository.TourExecutionRepository;
import com.soa.tour_service.repository.TourRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class TourExecutionSagaListener {

    private final TourRepository tourRepository;
    private final TourExecutionRepository tourExecutionRepository;
    private final RabbitTemplate rabbitTemplate;

    public TourExecutionSagaListener(
            TourRepository tourRepository,
            TourExecutionRepository tourExecutionRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.tourRepository = tourRepository;
        this.tourExecutionRepository = tourExecutionRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.TOUR_EXECUTION_TOKEN_RESERVED_QUEUE)
    @Transactional
    public void handleTokenReserved(TourExecutionTokenReservedEvent event) {
        try {
            Tour tour = tourRepository.findById(event.getTourId())
                    .orElseThrow(() -> new RuntimeException("Tura nije pronađena"));

            tourExecutionRepository.findByTouristIdAndStatus(
                    event.getTouristId(),
                    TourExecutionStatus.ACTIVE
            ).ifPresent(active -> {
                throw new RuntimeException("Već imate aktivnu turu");
            });

            LocalDateTime now = LocalDateTime.now();

            TourExecution execution = new TourExecution();
            execution.setTouristId(event.getTouristId());
            execution.setTour(tour);
            execution.setStatus(TourExecutionStatus.ACTIVE);
            execution.setStartedAt(now);
            execution.setLastActivityAt(now);
            execution.setStartLatitude(event.getLatitude());
            execution.setStartLongitude(event.getLongitude());

            tourExecutionRepository.save(execution);

        } catch (Exception e) {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TOURS_EXCHANGE,
                    RabbitMQConfig.TOUR_EXECUTION_START_FAILED_ROUTING_KEY,
                    new TourExecutionStartFailedEvent(
                            event.getTouristId(),
                            event.getTourId(),
                            "Tour service failed: " + e.getMessage()
                    )
            );
        }
    }
}