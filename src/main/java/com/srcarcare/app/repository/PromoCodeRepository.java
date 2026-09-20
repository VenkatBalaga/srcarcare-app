package com.srcarcare.app.repository;

import com.srcarcare.app.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    List<PromoCode> findAllByOrderByIdDesc();
    Optional<PromoCode> findByCodeIgnoreCase(String code);
}
