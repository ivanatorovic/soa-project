package com.soa.tour_service.service;

import com.soa.tour_service.dto.CreateTourRequest;
import com.soa.tour_service.dto.KeyPointResponse;
import com.soa.tour_service.dto.TourResponse;
import com.soa.tour_service.dto.TourTransportTimeResponse;
import com.soa.tour_service.model.*;
import com.soa.tour_service.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TourService {

    private final TourRepository tourRepository;
    private final TagRepository tagRepository;
    private final KeyPointRepository keyPointRepository;
    private final TourTransportTimeRepository tourTransportTimeRepository;
    private final RestTemplate restTemplate;

    public TourService(
            TourRepository tourRepository,
            TagRepository tagRepository,
            KeyPointRepository keyPointRepository,
            TourTransportTimeRepository tourTransportTimeRepository,
            RestTemplate restTemplate
    ) {
        this.tourRepository = tourRepository;
        this.tagRepository = tagRepository;
        this.keyPointRepository = keyPointRepository;
        this.tourTransportTimeRepository = tourTransportTimeRepository;
        this.restTemplate = restTemplate;
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

        if (request.getAvailableSlots() == null || request.getAvailableSlots() <= 0) {
            throw new RuntimeException("Broj slobodnih mesta mora biti veći od 0");
        }

        Tour tour = new Tour();
        tour.setName(request.getName());
        tour.setDescription(request.getDescription());
        tour.setDifficulty(request.getDifficulty());
        tour.setPrice(0.0);
        tour.setAvailableSlots(request.getAvailableSlots());
        tour.setStatus(TourStatus.DRAFT);
        tour.setAuthorId(authorId);
        tour.setTags(resolvedTags);

        Tour savedTour = tourRepository.save(tour);
        return mapToResponse(savedTour);
    }

    private void enrichTourForTourist(TourResponse response, Long touristId) {
        boolean purchased = Boolean.TRUE.equals(
                restTemplate.getForObject(
                        "http://purchase-service:8084/api/purchase/shopping-cart/tokens/exists?touristId="
                                + touristId + "&tourId=" + response.getId(),
                        Boolean.class
                )
        );

        response.setPurchased(purchased);

        boolean inCart = Boolean.TRUE.equals(
                restTemplate.getForObject(
                        "http://purchase-service:8084/api/purchase/shopping-cart/contains?touristId="
                                + touristId + "&tourId=" + response.getId(),
                        Boolean.class
                )
        );

        response.setInShoppingCart(inCart);
    }

    public List<TourResponse> getMyTours(Long authorId) {
        return tourRepository.findByAuthorId(authorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TourResponse publishTour(Long tourId, Long userId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tura nije pronađena"));

        if (!tour.getAuthorId().equals(userId)) {
            throw new RuntimeException("Niste vlasnik ove ture");
        }

        if (tour.getStatus() != TourStatus.DRAFT) {
            throw new RuntimeException("Samo tura u draft stanju može da se objavi");
        }

        validateTourForPublishing(tour);

        tour.setStatus(TourStatus.PUBLISHED);
        tour.setPublishedAt(LocalDateTime.now());

        Tour savedTour = tourRepository.save(tour);
        return mapToResponse(savedTour);
    }

    public TourResponse archiveTour(Long tourId, Long userId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tura nije pronađena"));

        if (!tour.getAuthorId().equals(userId)) {
            throw new RuntimeException("Niste vlasnik ove ture");
        }

        if (tour.getStatus() != TourStatus.PUBLISHED) {
            throw new RuntimeException("Samo objavljena tura može da se arhivira");
        }

        tour.setStatus(TourStatus.ARCHIVED);
        tour.setArchivedAt(LocalDateTime.now());

        Tour savedTour = tourRepository.save(tour);
        return mapToResponse(savedTour);
    }

    public TourResponse reactivateTour(Long tourId, Long userId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tura nije pronađena"));

        if (!tour.getAuthorId().equals(userId)) {
            throw new RuntimeException("Niste vlasnik ove ture");
        }

        if (tour.getStatus() != TourStatus.ARCHIVED) {
            throw new RuntimeException("Samo arhivirana tura može ponovo da se aktivira");
        }

        tour.setStatus(TourStatus.PUBLISHED);
        tour.setArchivedAt(null);

        Tour savedTour = tourRepository.save(tour);
        return mapToResponse(savedTour);
    }

    private void validateTourForPublishing(Tour tour) {
        if (tour.getName() == null || tour.getName().isBlank()) {
            throw new RuntimeException("Tura mora imati naziv");
        }

        if (tour.getDescription() == null || tour.getDescription().isBlank()) {
            throw new RuntimeException("Tura mora imati opis");
        }

        if (tour.getDifficulty() == null) {
            throw new RuntimeException("Tura mora imati težinu");
        }

        if (tour.getTags() == null || tour.getTags().isEmpty()) {
            throw new RuntimeException("Tura mora imati bar jedan tag");
        }

        if (tour.getKeyPoints() == null || tour.getKeyPoints().size() < 2) {
            throw new RuntimeException("Tura mora imati najmanje dve ključne tačke");
        }

        if (tour.getTransportTimes() == null || tour.getTransportTimes().isEmpty()) {
            throw new RuntimeException("Tura mora imati bar jedno vreme obilaska");
        }
    }

    public TourResponse addTransportTime(
            Long tourId,
            TransportType transportType,
            Integer durationMinutes,
            Long userId
    ) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tura nije pronađena"));

        if (!tour.getAuthorId().equals(userId)) {
            throw new RuntimeException("Niste vlasnik ove ture");
        }

        if (durationMinutes == null || durationMinutes <= 0) {
            throw new RuntimeException("Vreme obilaska mora biti veće od 0 minuta");
        }

        TourTransportTime transportTime = new TourTransportTime();
        transportTime.setTour(tour);
        transportTime.setTransportType(transportType);
        transportTime.setDurationMinutes(durationMinutes);

        tourTransportTimeRepository.save(transportTime);
        tour.getTransportTimes().add(transportTime);

        return mapToResponse(tour);
    }

    public List<TourResponse> getPublishedToursForTourist(Long touristId) {
        List<TourResponse> tours = tourRepository.findByStatus(TourStatus.PUBLISHED)
                .stream()
                .map(this::mapToTouristPreviewResponse)
                .toList();

        tours.forEach(tour -> enrichTourForTourist(tour, touristId));

        return tours;
    }

    private TourResponse mapToTouristPreviewResponse(Tour tour) {
        List<String> tagNames = tour.getTags()
                .stream()
                .map(Tag::getName)
                .toList();

        List<KeyPointResponse> keyPointResponses = tour.getKeyPoints()
                .stream()
                .limit(1)
                .map(kp -> new KeyPointResponse(
                        kp.getId(),
                        kp.getName(),
                        kp.getDescription(),
                        kp.getLatitude(),
                        kp.getLongitude(),
                        kp.getImageUrl()
                ))
                .toList();

        List<TourTransportTimeResponse> transportTimeResponses = tour.getTransportTimes()
                .stream()
                .map(time -> new TourTransportTimeResponse(
                        time.getId(),
                        time.getTransportType(),
                        time.getDurationMinutes()
                ))
                .toList();

        TourResponse response = new TourResponse(
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

        response.setPublishedAt(tour.getPublishedAt());
        response.setArchivedAt(tour.getArchivedAt());
        response.setDistanceInKm(tour.getDistanceInKm());
        response.setTransportTimes(transportTimeResponses);
        response.setAvailableSlots(tour.getAvailableSlots());

        return response;
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

        List<TourTransportTimeResponse> transportTimeResponses = tour.getTransportTimes()
                .stream()
                .map(time -> new TourTransportTimeResponse(
                        time.getId(),
                        time.getTransportType(),
                        time.getDurationMinutes()
                ))
                .toList();

        TourResponse response = new TourResponse(
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

        response.setPublishedAt(tour.getPublishedAt());
        response.setArchivedAt(tour.getArchivedAt());
        response.setDistanceInKm(tour.getDistanceInKm());
        response.setTransportTimes(transportTimeResponses);
        response.setAvailableSlots(tour.getAvailableSlots());

        return response;
    }

    public void addKeyPoint(
            Long tourId,
            String name,
            String description,
            Double latitude,
            Double longitude,
            MultipartFile image,
            Long userId
    ) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        if (!tour.getAuthorId().equals(userId)) {
            throw new RuntimeException("You are not the owner of this tour");
        }

        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            imageUrl = saveImage(image);
        }

        KeyPoint kp = new KeyPoint();
        kp.setName(name);
        kp.setDescription(description);
        kp.setLatitude(latitude);
        kp.setLongitude(longitude);
        kp.setImageUrl(imageUrl);
        kp.setTour(tour);

        keyPointRepository.save(kp);
        tour.getKeyPoints().add(kp);

        double totalDistance = calculateTotalTourDistance(tour.getKeyPoints());
        tour.setDistanceInKm(totalDistance);

        tourRepository.save(tour);
    }

    private String saveImage(MultipartFile image) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/keypoints";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalName = image.getOriginalFilename();
            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/keypoints/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }

    public TourResponse getTourById(Long tourId, Long userId, String role) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        if ("TOURIST".equals(role)) {
            boolean purchased = Boolean.TRUE.equals(
                    restTemplate.getForObject(
                            "http://purchase-service:8084/api/purchase/shopping-cart/tokens/exists?touristId="
                                    + userId + "&tourId=" + tourId,
                            Boolean.class
                    )
            );

            if (purchased) {
                return mapToResponse(tour);
            }

            return mapToTouristPreviewResponse(tour);
        }

        return mapToResponse(tour);
    }

    public List<TourResponse> getAllTours() {
        return tourRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void updateKeyPoint(
            Long tourId,
            Long keyPointId,
            String name,
            String description,
            Double latitude,
            Double longitude,
            MultipartFile image,
            Long userId
    ) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        if (!tour.getAuthorId().equals(userId)) {
            throw new RuntimeException("You are not the owner of this tour");
        }

        KeyPoint keyPoint = keyPointRepository.findById(keyPointId)
                .orElseThrow(() -> new RuntimeException("Key point not found"));

        if (keyPoint.getTour() == null || !keyPoint.getTour().getId().equals(tourId)) {
            throw new RuntimeException("Key point does not belong to this tour");
        }

        keyPoint.setName(name);
        keyPoint.setDescription(description);
        keyPoint.setLatitude(latitude);
        keyPoint.setLongitude(longitude);

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            keyPoint.setImageUrl(imageUrl);
        }

        keyPointRepository.save(keyPoint);

        double totalDistance = calculateTotalTourDistance(tour.getKeyPoints());
        tour.setDistanceInKm(totalDistance);

        tourRepository.save(tour);
    }

    public void deleteKeyPoint(Long tourId, Long keyPointId, Long userId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        if (!tour.getAuthorId().equals(userId)) {
            throw new RuntimeException("You are not the owner of this tour");
        }

        KeyPoint keyPoint = keyPointRepository.findById(keyPointId)
                .orElseThrow(() -> new RuntimeException("Key point not found"));

        if (keyPoint.getTour() == null || !keyPoint.getTour().getId().equals(tourId)) {
            throw new RuntimeException("Key point does not belong to this tour");
        }

        keyPointRepository.delete(keyPoint);
        tour.getKeyPoints().remove(keyPoint);

        double totalDistance = calculateTotalTourDistance(tour.getKeyPoints());
        tour.setDistanceInKm(totalDistance);

        tourRepository.save(tour);
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

    private double calculateTotalTourDistance(List<KeyPoint> keyPoints) {
        if (keyPoints == null || keyPoints.size() < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;

        for (int i = 1; i < keyPoints.size(); i++) {
            KeyPoint previous = keyPoints.get(i - 1);
            KeyPoint current = keyPoints.get(i);

            totalDistance += calculateDistanceInKm(
                    previous.getLatitude(),
                    previous.getLongitude(),
                    current.getLatitude(),
                    current.getLongitude()
            );
        }

        return totalDistance;
    }
}