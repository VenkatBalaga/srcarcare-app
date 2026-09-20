package com.srcarcare.app.service;

import com.srcarcare.app.entity.Banner;
import com.srcarcare.app.repository.BannerRepository;
import com.srcarcare.app.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerService {

    private final BannerRepository bannerRepository;

    public BannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    public List<Banner> listAll() {
        return bannerRepository.findAllByOrderByDisplayOrderAsc();
    }

    public List<Banner> listActive() {
        return bannerRepository.findByActiveTrueOrderByDisplayOrderAsc();
    }

    public Banner getById(Long id) {
        return bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner not found with id " + id));
    }

    public Banner save(Banner banner) {
        return bannerRepository.save(banner);
    }

    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }
}
