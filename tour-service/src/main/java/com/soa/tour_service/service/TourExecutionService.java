package com.soa.tour_service.service;


import com.soa.tour_service.config.RabbitMQConfig;
import com.soa.tour_service.dto.*;
import com.soa.tour_service.events.StartTourExecutionRequestedEvent;
import com.soa.tour_service.events.TourExecutionAbandonedEvent;
import com.soa.tour_service.events.TourExecutionCompletedEvent;
import com.soa.tour_service.model.*;
import com.soa.tour_service.repository.CompletedKeyPointRepository;
import com.soa.tour_service.repository.TourExecutionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.client.RestTemplate;
import com.soa.tour_service.repository.TourRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TourExecutionService {

    private static final double KEY_POINT_RADIUS_KM = 0.2;

    private final TourExecutionRepository tourExecutionRepository;
    private final CompletedKeyPointRepository completedKeyPointRepository;
    private final TourRepository tourRepository;
    private final RabbitTemplate rabbitTemplate;


    public TourExecutionService(
            TourExecutionRepository tourExecutionRepository,
            CompletedKeyPointRepository completedKeyPointRepository,
            TourRepository tourRepository,
            RabbitTemplate rabbitTemplate
    ) {
        this.tourExecutionRepository = tourExecutionRepository;
        this.completedKeyPointRepository = completedKeyPointRepository;
        this.tourRepository = tourRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public String startTour(Long touristId, Long tourId, StartTourExecutionRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tura nije pronađena"));

        if (tour.getStatus() != TourStatus.PUBLISHED && tour.getStatus() != TourStatus.ARCHIVED) {
            throw new RuntimeException("Moguće je pokrenuti samo objavljenu ili arhiviranu turu");
        }

        tourExecutionRepository.findByTouristIdAndStatus(touristId, TourExecutionStatus.ACTIVE)
                .ifPresent(active -> {
                    throw new RuntimeException("Već imate aktivnu turu");
                });


        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOURS_EXCHANGE,
                RabbitMQConfig.TOUR_EXECUTION_START_REQUESTED_ROUTING_KEY,
                new StartTourExecutionRequestedEvent(
                        touristId,
                        tourId,
                        request.getLatitude(),
                        request.getLongitude()
                )
        );

        return "Pokretanje ture je započeto";
    }

    public TourExecutionResponse checkKeyPoints(Long touristId, Long executionId, CheckKeyPointRequest request) {
        TourExecution execution = getActiveExecution(touristId, executionId);

        LocalDateTime now = LocalDateTime.now();
        execution.setLastActivityAt(now);

        List<KeyPoint> keyPoints = execution.getTour().getKeyPoints();

        for (KeyPoint keyPoint : keyPoints) {
            boolean alreadyCompleted =
                    execution.getCompletedKeyPoints()
                            .stream()
                            .anyMatch(completed ->
                                    completed.getKeyPointId().equals(keyPoint.getId())
                            )
                            ||
                            completedKeyPointRepository.existsByTourExecutionIdAndKeyPointId(
                                    execution.getId(),
                                    keyPoint.getId()
                            );

            if (alreadyCompleted) {
                continue;
            }

            double distance = calculateDistanceInKm(
                    request.getLatitude(),
                    request.getLongitude(),
                    keyPoint.getLatitude(),
                    keyPoint.getLongitude()
            );

            if (distance <= KEY_POINT_RADIUS_KM) {
                CompletedKeyPoint completed = new CompletedKeyPoint();
                completed.setTourExecution(execution);
                completed.setKeyPointId(keyPoint.getId());
                completed.setKeyPointName(keyPoint.getName());
                completed.setReachedAt(now);

                execution.getCompletedKeyPoints().add(completed);
            }
        }

        int totalKeyPoints = keyPoints.size();

        long completedCount = execution.getCompletedKeyPoints()
                .stream()
                .map(CompletedKeyPoint::getKeyPointId)
                .distinct()
                .count();

        if (totalKeyPoints > 0 && completedCount == totalKeyPoints) {
            execution.setStatus(TourExecutionStatus.COMPLETED);
            execution.setCompletedAt(now);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TOURS_EXCHANGE,
                    RabbitMQConfig.TOUR_EXECUTION_COMPLETED_ROUTING_KEY,
                    new TourExecutionCompletedEvent(
                            touristId,
                            execution.getTour().getId()
                    )
            );
        }

        TourExecution saved = tourExecutionRepository.save(execution);
        return mapToResponse(saved);
    }

    public TourExecutionResponse completeTour(Long touristId, Long executionId) {
        TourExecution execution = getActiveExecution(touristId, executionId);

        LocalDateTime now = LocalDateTime.now();
        execution.setStatus(TourExecutionStatus.COMPLETED);
        execution.setCompletedAt(now);
        execution.setLastActivityAt(now);

        TourExecution saved = tourExecutionRepository.save(execution);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOURS_EXCHANGE,
                RabbitMQConfig.TOUR_EXECUTION_COMPLETED_ROUTING_KEY,
                new TourExecutionCompletedEvent(
                        touristId,
                        execution.getTour().getId()
                )
        );

        return mapToResponse(saved);
    }

    public TourExecutionResponse abandonTour(Long touristId, Long executionId) {
        TourExecution execution = getActiveExecution(touristId, executionId);

        LocalDateTime now = LocalDateTime.now();
        execution.setStatus(TourExecutionStatus.ABANDONED);
        execution.setAbandonedAt(now);
        execution.setLastActivityAt(now);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOURS_EXCHANGE,
                RabbitMQConfig.TOUR_EXECUTION_ABANDONED_ROUTING_KEY,
                new TourExecutionAbandonedEvent(
                        touristId,
                        execution.getTour().getId()
                )
        );
        return mapToResponse(tourExecutionRepository.save(execution));
    }

    public TourExecutionResponse getActiveTour(Long touristId) {
        return tourExecutionRepository
                .findByTouristIdAndStatus(touristId, TourExecutionStatus.ACTIVE)
                .map(this::mapToResponse)
                .orElse(null);
    }

    private TourExecution getActiveExecution(Long touristId, Long executionId) {
        TourExecution execution = tourExecutionRepository.findByIdAndTouristId(executionId, touristId)
                .orElseThrow(() -> new RuntimeException("Aktivna sesija nije pronađena"));

        if (execution.getStatus() != TourExecutionStatus.ACTIVE) {
            throw new RuntimeException("Sesija nije aktivna");
        }

        return execution;
    }

    private TourExecutionResponse mapToResponse(TourExecution execution) {
        List<CompletedKeyPointResponse> completed =
                execution.getCompletedKeyPoints() == null
                        ? List.of()
                        : execution.getCompletedKeyPoints()
                        .stream()
                        .map(kp -> new CompletedKeyPointResponse(
                                kp.getKeyPointId(),
                                kp.getKeyPointName(),
                                kp.getReachedAt()
                        ))
                        .toList();

        return new TourExecutionResponse(
                execution.getId(),
                execution.getTour().getId(),
                execution.getTour().getName(),
                execution.getStatus(),
                execution.getStartedAt(),
                execution.getCompletedAt(),
                execution.getAbandonedAt(),
                execution.getLastActivityAt(),
                completed
        );
    }

    private double calculateDistanceInKm(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int earthRadiusKm = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadiusKm * c;
    }

    public List<TourExecutionResponse> getCompletedExecutions(Long touristId) {

        List<TourExecution> executions =
                tourExecutionRepository.findByTouristIdAndStatusIn(
                        touristId,
                        List.of(
                                TourExecutionStatus.COMPLETED,
                                TourExecutionStatus.ABANDONED
                        )
                );

        return executions.stream()
                .map(this::mapToResponse)
                .toList();
    }
}