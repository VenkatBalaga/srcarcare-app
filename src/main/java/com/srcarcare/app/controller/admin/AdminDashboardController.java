package com.srcarcare.app.controller.admin;

import com.srcarcare.app.entity.BookingStatus;
import com.srcarcare.app.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final RentalBookingService rentalBookingService;
    private final CarWashService carWashService;
    private final CarServiceBookingService carServiceBookingService;
    private final ReviewService reviewService;

    public AdminDashboardController(RentalBookingService rentalBookingService, CarWashService carWashService,
                                     CarServiceBookingService carServiceBookingService, ReviewService reviewService) {
        this.rentalBookingService = rentalBookingService;
        this.carWashService = carWashService;
        this.carServiceBookingService = carServiceBookingService;
        this.reviewService = reviewService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("active", "dashboard");

        model.addAttribute("totalRentalBookings", rentalBookingService.countAll());
        model.addAttribute("pendingRentalBookings", rentalBookingService.countByStatus(BookingStatus.PENDING));
        model.addAttribute("totalWashRequests", carWashService.countAll());
        model.addAttribute("pendingWashRequests", carWashService.countByStatus(BookingStatus.PENDING));
        model.addAttribute("totalServiceRequests", carServiceBookingService.countAll());
        model.addAttribute("pendingServiceRequests", carServiceBookingService.countByStatus(BookingStatus.PENDING));

        model.addAttribute("recentBookings", rentalBookingService.recent(10));
        model.addAttribute("upcomingBookings", rentalBookingService.upcoming());
        model.addAttribute("recentWashRequests", carWashService.recent());
        model.addAttribute("recentServiceRequests", carServiceBookingService.recent());
        model.addAttribute("recentReviews", reviewService.listAll().stream().limit(6).toList());

        return "admin/dashboard";
    }
}
