package com.soa.tour_service.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public class CreateReviewRequest {

    private Integer rating;
    private String comment;
    private LocalDate visitedAt;
    private List<MultipartFile> images;

    public CreateReviewRequest() {
    }

    public Integer getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDate getVisitedAt() {
        return visitedAt;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setVisitedAt(LocalDate visitedAt) {
        this.visitedAt = visitedAt;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}