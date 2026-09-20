package com.srcarcare.app.controller;

import com.srcarcare.app.service.CarWashService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/car-wash")
public class CarWashController {

    private final CarWashService carWashService;

    public CarWashController(CarWashService carWashService) {
        this.carWashService = carWashService;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("active", "wash");
        model.addAttribute("options", carWashService.listActiveOptions());
        return "car-wash";
    }

    @PostMapping("/book")
    public String book(@RequestParam Long optionId,
                        @RequestParam String customerName,
                        @RequestParam String customerPhone,
                        @RequestParam(required = false) String customerEmail,
                        @RequestParam(required = false) String vehicleInfo,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate preferredDate,
                        @RequestParam(required = false) String address,
                        RedirectAttributes redirectAttributes) {
        try {
            carWashService.createBooking(optionId, customerName, customerPhone, customerEmail, vehicleInfo, preferredDate, address);
            redirectAttributes.addFlashAttribute("success", "Your car wash request has been received! We will contact you shortly to confirm.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/car-wash";
    }
}
