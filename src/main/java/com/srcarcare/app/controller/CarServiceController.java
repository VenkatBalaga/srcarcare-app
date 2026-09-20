package com.srcarcare.app.controller;

import com.srcarcare.app.service.CarServiceBookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/car-service")
public class CarServiceController {

    private final CarServiceBookingService carServiceBookingService;

    public CarServiceController(CarServiceBookingService carServiceBookingService) {
        this.carServiceBookingService = carServiceBookingService;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("active", "service");
        model.addAttribute("options", carServiceBookingService.listActiveOptions());
        return "car-service";
    }

    @PostMapping("/book")
    public String book(@RequestParam Long optionId,
                        @RequestParam String customerName,
                        @RequestParam String customerPhone,
                        @RequestParam(required = false) String customerEmail,
                        @RequestParam(required = false) String vehicleInfo,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate preferredDate,
                        @RequestParam(required = false) String issueDescription,
                        RedirectAttributes redirectAttributes) {
        try {
            carServiceBookingService.createBooking(optionId, customerName, customerPhone, customerEmail, vehicleInfo, preferredDate, issueDescription);
            redirectAttributes.addFlashAttribute("success", "Your service request has been received! We will contact you shortly to confirm.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/car-service";
    }
}
