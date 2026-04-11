package com.soa.tour_service.service;



import com.soa.tour_service.dto.CreateKeyPointRequest;
import com.soa.tour_service.dto.CreateTourRequest;
import com.soa.tour_service.dto.KeyPointResponse;
import com.soa.tour_service.dto.TourResponse;
import com.soa.tour_service.model.KeyPoint;
import com.soa.tour_service.model.Tag;
import com.soa.tour_service.model.Tour;
import com.soa.tour_service.model.TourStatus;
import com.soa.tour_service.repository.KeyPointRepository;
import com.soa.tour_service.repository.TagRepository;
import com.soa.tour_service.repository.TourRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
public class TourService {

    private final TourRepository tourRepository;
    private final TagRepository tagRepository;
    private final KeyPointRepository keyPointRepository;

    public TourService(TourRepository tourRepository, TagRepository tagRepository,KeyPointRepository keyPointRepository) {
        this.tourRepository = tourRepository;
        this.tagRepository = tagRepository;
        this.keyPointRepository = keyPointRepository;
    }

    public TourResponse createTour(CreateTourRequest request, Long authorId) {
        Set<Tag> resolvedTags = new HashSet<>();

        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                String normalized = tagName.trim();

                if (normalized.isEmpty()) {
                    continue;
                }

                Tag tag = tagRepository.findByName(normalized)
                        .orElseGet(() -> tagRepository.save(new Tag(normalized)));

                resolvedTags.add(tag);
            }
        }

        Tour tour = new Tour();
        tour.setName(request.getName());
        tour.setDescription(request.getDescription());
        tour.setDifficulty(request.getDifficulty());
        tour.setPrice(0.0);
        tour.setStatus(TourStatus.DRAFT);
        tour.setAuthorId(authorId);
        tour.setTags(resolvedTags);

        Tour savedTour = tourRepository.save(tour);
        return mapToResponse(savedTour);
    }

    public List<TourResponse> getMyTours(Long authorId) {
        return tourRepository.findByAuthorId(authorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TourResponse mapToResponse(Tour tour) {
        List<String> tagNames = tour.getTags()
                .stream()
                .map(Tag::getName)
                .toList();

        List<KeyPointResponse> keyPointResponses = tour.getKeyPoints()
                .stream()
                .map(kp -> new KeyPointResponse(
                        kp.getId(),
                        kp.getName(),
                        kp.getDescription(),
                        kp.getLatitude(),
                        kp.getLongitude(),
                        kp.getImageUrl()
                ))
                .toList();

        return new TourResponse(
                tour.getId(),
                tour.getName(),
                tour.getDescription(),
                tour.getDifficulty(),
                tour.getPrice(),
                tour.getStatus(),
                tour.getAuthorId(),
                tagNames,
                keyPointResponses
        );
    }

    public void addKeyPoint(Long tourId, CreateKeyPointRequest request, Long userId) {

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        // PROVERA: da li je to autor
        if (!tour.getAuthorId().equals(userId)) {
            throw new RuntimeException("You are not the owner of this tour");
        }

        KeyPoint kp = new KeyPoint();
        kp.setName(request.name);
        kp.setDescription(request.description);
        kp.setLatitude(request.latitude);
        kp.setLongitude(request.longitude);
        kp.setImageUrl(request.imageUrl);
        kp.setTour(tour);

        keyPointRepository.save(kp);
    }

    public TourResponse getTourById(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        return mapToResponse(tour);
    }
}
