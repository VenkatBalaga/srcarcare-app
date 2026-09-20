package com.srcarcare.app.controller;

import com.srcarcare.app.entity.RentalBooking;
import com.srcarcare.app.entity.RentalType;
import com.srcarcare.app.entity.Vehicle;
import com.srcarcare.app.service.RentalBookingService;
import com.srcarcare.app.service.VehicleService;
import com.srcarcare.app.util.BookingConflictException;
import com.srcarcare.app.util.InvalidPromoCodeException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/rent-a-car")
public class RentalController {

    private final VehicleService vehicleService;
    private final RentalBookingService rentalBookingService;

    public RentalController(VehicleService vehicleService, RentalBookingService rentalBookingService) {
        this.vehicleService = vehicleService;
        this.rentalBookingService = rentalBookingService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Integer seating,
                        @RequestParam(required = false) RentalType rentalType,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                        Model model) {
        model.addAttribute("active", "rent");
        model.addAttribute("vehicles", vehicleService.searchAvailable(seating, rentalType, startDate, endDate));
        model.addAttribute("seating", seating);
        model.addAttribute("rentalType", rentalType);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "rent-a-car";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                          @RequestParam(required = false) RentalType rentalType,
                          Model model) {
        Vehicle vehicle = vehicleService.getById(id);
        model.addAttribute("active", "rent");
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("rentalType", rentalType);
        return "vehicle-detail";
    }

    @PostMapping("/{id}/book")
    public String book(@PathVariable Long id,
                        @RequestParam String customerName,
                        @RequestParam String customerPhone,
                        @RequestParam(required = false) String customerEmail,
                        @RequestParam RentalType rentalType,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                        @RequestParam(required = false) String promoCode,
                        RedirectAttributes redirectAttributes) {
        try {
            RentalBooking booking = rentalBookingService.createBooking(id, customerName, customerPhone, customerEmail,
                    rentalType, startDate, endDate, promoCode);
            redirectAttributes.addFlashAttribute("success", "Your booking request has been received! Our team will confirm it shortly.");
            return "redirect:/rent-a-car/confirmation/" + booking.getId();
        } catch (BookingConflictException | InvalidPromoCodeException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/rent-a-car/" + id;
        }
    }

    @GetMapping("/confirmation/{bookingId}")
    public String confirmation(@PathVariable Long bookingId, Model model) {
        model.addAttribute("active", "rent");
        model.addAttribute("booking", rentalBookingService.getById(bookingId));
        return "booking-confirmation";
    }
}
