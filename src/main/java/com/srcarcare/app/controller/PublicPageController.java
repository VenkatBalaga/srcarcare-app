package com.srcarcare.app.controller;

import com.srcarcare.app.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicPageController {

    private final VehicleService vehicleService;
    private final BannerService bannerService;
    private final OfferService offerService;
    private final ReviewService reviewService;

    public PublicPageController(VehicleService vehicleService, BannerService bannerService,
                                 OfferService offerService, ReviewService reviewService) {
        this.vehicleService = vehicleService;
        this.bannerService = bannerService;
        this.offerService = offerService;
        this.reviewService = reviewService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("active", "home");
        model.addAttribute("banners", bannerService.listActive());
        model.addAttribute("offers", offerService.listCurrentlyRunning());
        model.addAttribute("vehicles", vehicleService.listActive().stream().limit(6).toList());
        model.addAttribute("reviews", reviewService.recentApproved());
        return "home";
    }
}
