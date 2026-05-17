package com.soa.tour_service.messaging;

import com.soa.tour_service.config.RabbitMQConfig;
import com.soa.tour_service.events.PurchaseStartedEvent;
import com.soa.tour_service.events.PurchaseTourItem;
import com.soa.tour_service.events.ToursReservationCancelEvent;
import com.soa.tour_service.events.ToursReservationFailedEvent;
import com.soa.tour_service.events.ToursReservedEvent;
import com.soa.tour_service.model.Tour;
import com.soa.tour_service.model.TourStatus;
import com.soa.tour_service.repository.TourRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class TourReservationSagaListener {

    private final TourRepository tourRepository;
    private final RabbitTemplate rabbitTemplate;

    public TourReservationSagaListener(TourRepository tourRepository, RabbitTemplate rabbitTemplate) {
        this.tourRepository = tourRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.PURCHASE_STARTED_QUEUE)
    @Transactional
    public void handlePurchaseStarted(PurchaseStartedEvent event) {
        List<PurchaseTourItem> reservedItems = new ArrayList<>();

        try {
            for (PurchaseTourItem item : event.getItems()) {
                Tour tour = tourRepository.findById(item.getTourId())
                        .orElseThrow(() -> new RuntimeException("Tura nije pronađena: " + item.getTourId()));

                if (tour.getStatus() != TourStatus.PUBLISHED) {
                    throw new RuntimeException("Tura nije objavljena: " + tour.getId());
                }

                if (tour.getAvailableSlots() == null || tour.getAvailableSlots() <= 0) {
                    throw new RuntimeException("Nema slobodnih mesta za turu: " + tour.getName());
                }

                tour.setAvailableSlots(tour.getAvailableSlots() - 1);
                tourRepository.save(tour);

                reservedItems.add(new PurchaseTourItem(tour.getId()));
            }

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TOURS_EXCHANGE,
                    RabbitMQConfig.TOURS_RESERVED_ROUTING_KEY,
                    new ToursReservedEvent(
                            event.getPurchaseId(),
                            event.getTouristId(),
                            event.getItems()
                    )
            );

        } catch (Exception e) {
            for (PurchaseTourItem item : reservedItems) {
                tourRepository.findById(item.getTourId()).ifPresent(tour -> {
                    tour.setAvailableSlots(tour.getAvailableSlots() + 1);
                    tourRepository.save(tour);
                });
            }

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TOURS_EXCHANGE,
                    RabbitMQConfig.TOURS_RESERVATION_FAILED_ROUTING_KEY,
                    new ToursReservationFailedEvent(
                            event.getPurchaseId(),
                            event.getTouristId(),
                            e.getMessage()
                    )
            );
        }
    }

    @RabbitListener(queues = RabbitMQConfig.TOURS_RESERVATION_CANCEL_QUEUE)
    @Transactional
    public void handleReservationCancel(ToursReservationCancelEvent event) {
        for (PurchaseTourItem item : event.getItems()) {
            tourRepository.findById(item.getTourId()).ifPresent(tour -> {
                tour.setAvailableSlots(tour.getAvailableSlots() + 1);
                tourRepository.save(tour);
            });
        }
    }
}