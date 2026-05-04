package com.soa.tour_service.service;

import com.soa.tour_service.dto.CreateReviewRequest;
import com.soa.tour_service.dto.ReviewResponse;
import com.soa.tour_service.model.Review;
import com.soa.tour_service.model.Tour;
import com.soa.tour_service.repository.ReviewRepository;
import com.soa.tour_service.repository.TourRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TourRepository tourRepository;

    public ReviewService(ReviewRepository reviewRepository, TourRepository tourRepository) {
        this.reviewRepository = reviewRepository;
        this.tourRepository = tourRepository;
    }

    public ReviewResponse createReview(
            Long tourId,
            CreateReviewRequest request,
            Long touristId,
            String touristUsername,
            String role
    ) {
        if (!"TOURIST".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only tourists can leave reviews"
            );
        }

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tour with id " + tourId + " not found"
                ));

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rating must be between 1 and 5"
            );
        }

        Review review = new Review();
        review.setTour(tour);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setVisitedAt(request.getVisitedAt());
        review.setCreatedAt(LocalDateTime.now());
        review.setTouristId(touristId);
        review.setTouristUsername(touristUsername);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<String> imageUrls = request.getImages().stream()
                    .filter(image -> image != null && !image.isEmpty())
                    .map(this::saveImage)
                    .toList();

            review.setImageUrls(imageUrls);
        }

        Review savedReview = reviewRepository.save(review);

        return mapToResponse(savedReview);
    }

    public List<ReviewResponse> getReviewsForTour(Long tourId) {
        return reviewRepository.findByTourId(tourId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public long countReviewsForTour(Long tourId) {
        return reviewRepository.countByTourId(tourId);
    }

    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getTour().getId(),
                review.getRating(),
                review.getComment(),
                review.getTouristId(),
                review.getTouristUsername(),
                review.getVisitedAt(),
                review.getCreatedAt(),
                review.getImageUrls()
        );
    }

    private String saveImage(MultipartFile image) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/reviews";
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

            return "/uploads/reviews/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save review image", e);
        }
    }
}