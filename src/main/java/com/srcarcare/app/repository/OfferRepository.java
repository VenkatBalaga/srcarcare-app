package com.srcarcare.app.repository;

import com.srcarcare.app.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findAllByOrderByIdDesc();
    List<Offer> findByActiveTrue();
}
