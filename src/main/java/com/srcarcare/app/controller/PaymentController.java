package com.srcarcare.app.controller;

import com.srcarcare.app.entity.PaymentTransaction;
import com.srcarcare.app.entity.RentalBooking;
import com.srcarcare.app.service.PhonePeService;
import com.srcarcare.app.service.RentalBookingService;
import com.srcarcare.app.util.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PhonePeService phonePeService;
    private final RentalBookingService rentalBookingService;

    public PaymentController(PhonePeService phonePeService, RentalBookingService rentalBookingService) {
        this.phonePeService = phonePeService;
        this.rentalBookingService = rentalBookingService;
    }

    @PostMapping("/initiate")
    public String initiate(@RequestParam String bookingType, @RequestParam Long bookingId, Model model) {
        if (!"RENTAL".equalsIgnoreCase(bookingType)) {
            // Wash / service payments are collected on-site in this build; only rental deposits go through PhonePe.
            return "redirect:/";
        }
        RentalBooking booking = rentalBookingService.getById(bookingId);
        PhonePeService.PaymentInitiationResult result = phonePeService.initiatePayment("RENTAL", bookingId, booking.getTotalPrice());
        return "redirect:" + result.redirectUrl();
    }

    @GetMapping("/not-configured")
    public String notConfigured(@RequestParam String txn, Model model) {
        model.addAttribute("active", "payment");
        model.addAttribute("txn", txn);
        return "payment-not-configured";
    }

    @GetMapping("/redirect")
    public String redirect(@RequestParam String txn, Model model) {
        PaymentTransaction transaction = phonePeService.getTransaction(txn)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found"));
        model.addAttribute("active", "payment");
        model.addAttribute("transaction", transaction);
        return "payment-status";
    }
}
