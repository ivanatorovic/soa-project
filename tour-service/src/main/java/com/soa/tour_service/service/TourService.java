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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

    public TourResponse getTourById(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

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
    }
}
