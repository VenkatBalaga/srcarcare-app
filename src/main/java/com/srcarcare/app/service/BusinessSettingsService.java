package com.srcarcare.app.service;

import com.srcarcare.app.entity.BusinessSettings;
import com.srcarcare.app.repository.BusinessSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class BusinessSettingsService {

    private final BusinessSettingsRepository businessSettingsRepository;

    public BusinessSettingsService(BusinessSettingsRepository businessSettingsRepository) {
        this.businessSettingsRepository = businessSettingsRepository;
    }

    public BusinessSettings get() {
        return businessSettingsRepository.findById(1L)
                .orElseGet(() -> {
                    BusinessSettings settings = new BusinessSettings();
                    settings.setId(1L);
                    return businessSettingsRepository.save(settings);
                });
    }

    public BusinessSettings save(BusinessSettings settings) {
        settings.setId(1L);
        return businessSettingsRepository.save(settings);
    }
}
