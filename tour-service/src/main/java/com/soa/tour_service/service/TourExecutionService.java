package com.soa.tour_service.service;

import com.soa.tour_service.dto.*;
import com.soa.tour_service.model.*;
import com.soa.tour_service.repository.CompletedKeyPointRepository;
import com.soa.tour_service.repository.TourExecutionRepository;
import com.soa.tour_service.repository.TourPurchaseTokenRepository;
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
    private final TourPurchaseTokenRepository tokenRepository;

    public TourExecutionService(
            TourExecutionRepository tourExecutionRepository,
            CompletedKeyPointRepository completedKeyPointRepository,
            TourRepository tourRepository,
            TourPurchaseTokenRepository tokenRepository
    ) {
        this.tourExecutionRepository = tourExecutionRepository;
        this.completedKeyPointRepository = completedKeyPointRepository;
        this.tourRepository = tourRepository;
        this.tokenRepository = tokenRepository;
    }

    public TourExecutionResponse startTour(Long touristId, Long tourId, StartTourExecutionRequest request) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tura nije pronađena"));
        boolean alreadyFinishedOrAbandoned =
                tourExecutionRepository.existsByTouristIdAndTourIdAndStatusIn(
                        touristId,
                        tourId,
                        List.of(TourExecutionStatus.COMPLETED, TourExecutionStatus.ABANDONED)
                );

        if (alreadyFinishedOrAbandoned) {
            throw new RuntimeException("Ovu turu ste već završili ili napustili i ne možete je ponovo pokrenuti");
        }

        if (tour.getStatus() != TourStatus.PUBLISHED && tour.getStatus() != TourStatus.ARCHIVED) {
            throw new RuntimeException("Moguće je pokrenuti samo objavljenu ili arhiviranu turu");
        }

        boolean purchased = tokenRepository.existsByTouristIdAndTourId(touristId, tourId);
        if (!purchased) {
            throw new RuntimeException("Morate kupiti turu pre pokretanja");
        }

        tourExecutionRepository.findByTouristIdAndStatus(touristId, TourExecutionStatus.ACTIVE)
                .ifPresent(active -> {
                    throw new RuntimeException("Već imate aktivnu turu");
                });

        LocalDateTime now = LocalDateTime.now();

        TourExecution execution = new TourExecution();
        execution.setTouristId(touristId);
        execution.setTour(tour);
        execution.setStatus(TourExecutionStatus.ACTIVE);
        execution.setStartedAt(now);
        execution.setLastActivityAt(now);
        execution.setStartLatitude(request.getLatitude());
        execution.setStartLongitude(request.getLongitude());

        TourExecution saved = tourExecutionRepository.save(execution);
        return mapToResponse(saved);
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

        return mapToResponse(tourExecutionRepository.save(execution));
    }

    public TourExecutionResponse abandonTour(Long touristId, Long executionId) {
        TourExecution execution = getActiveExecution(touristId, executionId);

        LocalDateTime now = LocalDateTime.now();
        execution.setStatus(TourExecutionStatus.ABANDONED);
        execution.setAbandonedAt(now);
        execution.setLastActivityAt(now);

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
        List<CompletedKeyPointResponse> completed = execution.getCompletedKeyPoints()
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