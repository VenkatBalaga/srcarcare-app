package com.srcarcare.app.service;

import com.srcarcare.app.entity.Review;
import com.srcarcare.app.entity.ReviewImage;
import com.srcarcare.app.repository.ReviewRepository;
import com.srcarcare.app.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FileStorageService fileStorageService;

    public ReviewService(ReviewRepository reviewRepository, FileStorageService fileStorageService) {
        this.reviewRepository = reviewRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public Review submit(String customerName, Integer rating, String reviewText, List<MultipartFile> photos) {
        Review review = new Review();
        review.setCustomerName(customerName);
        review.setRating(rating);
        review.setReviewText(reviewText);
        review.setApproved(false);
        review.setHidden(false);
        Review saved = reviewRepository.save(review);

        if (photos != null) {
            for (MultipartFile photo : photos) {
                if (photo != null && !photo.isEmpty()) {
                    String path = fileStorageService.store(photo, "reviews");
                    ReviewImage image = new ReviewImage();
                    image.setReview(saved);
                    image.setImagePath(path);
                    saved.getImages().add(image);
                }
            }
        }
        return reviewRepository.save(saved);
    }

    public List<Review> listAll() {
        return reviewRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Review> listApproved() {
        return reviewRepository.findByApprovedTrueAndHiddenFalseOrderByCreatedAtDesc();
    }

    public List<Review> recentApproved() {
        return reviewRepository.findTop10ByApprovedTrueAndHiddenFalseOrderByCreatedAtDesc();
    }

    public Review getById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + id));
    }

    public void approve(Long id) {
        Review review = getById(id);
        review.setApproved(true);
        review.setHidden(false);
        reviewRepository.save(review);
    }

    public void hide(Long id) {
        Review review = getById(id);
        review.setHidden(true);
        reviewRepository.save(review);
    }

    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }
}
