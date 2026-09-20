package com.srcarcare.app.repository;

import com.srcarcare.app.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findAllByOrderByDisplayOrderAsc();
    List<Banner> findByActiveTrueOrderByDisplayOrderAsc();
}
