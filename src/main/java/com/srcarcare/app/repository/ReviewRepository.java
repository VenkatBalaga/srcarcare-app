package com.srcarcare.app.repository;

import com.srcarcare.app.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByOrderByCreatedAtDesc();
    List<Review> findByApprovedTrueAndHiddenFalseOrderByCreatedAtDesc();
    List<Review> findTop10ByApprovedTrueAndHiddenFalseOrderByCreatedAtDesc();
}
