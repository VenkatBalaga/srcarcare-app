package com.srcarcare.app.config;

import com.srcarcare.app.entity.BusinessSettings;
import com.srcarcare.app.service.BusinessSettingsService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.srcarcare.app.controller.PublicPageController;

@ControllerAdvice(assignableTypes = {
        PublicPageController.class,
        com.srcarcare.app.controller.RentalController.class,
        com.srcarcare.app.controller.CarWashController.class,
        com.srcarcare.app.controller.CarServiceController.class,
        com.srcarcare.app.controller.ReviewController.class,
        com.srcarcare.app.controller.PaymentController.class
})
public class PublicModelAdvice {

    private final BusinessSettingsService businessSettingsService;

    public PublicModelAdvice(BusinessSettingsService businessSettingsService) {
        this.businessSettingsService = businessSettingsService;
    }

    @ModelAttribute("businessSettings")
    public BusinessSettings businessSettings() {
        return businessSettingsService.get();
    }
}
