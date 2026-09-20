package com.srcarcare.app.controller.admin;

import com.srcarcare.app.entity.BookingStatus;
import com.srcarcare.app.entity.RentalBooking;
import com.srcarcare.app.service.RentalBookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final RentalBookingService rentalBookingService;

    public AdminBookingController(RentalBookingService rentalBookingService) {
        this.rentalBookingService = rentalBookingService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String customer,
                        @RequestParam(required = false) String vehicle,
                        @RequestParam(required = false) BookingStatus status,
                        @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                        Model model) {
        List<RentalBooking> bookings = rentalBookingService.listAll();

        if (customer != null && !customer.isBlank()) {
            String q = customer.trim().toLowerCase();
            bookings = bookings.stream().filter(b -> b.getCustomerName().toLowerCase().contains(q)
                    || b.getCustomerPhone().toLowerCase().contains(q)).toList();
        }
        if (vehicle != null && !vehicle.isBlank()) {
            String q = vehicle.trim().toLowerCase();
            bookings = bookings.stream().filter(b -> b.getVehicle().getName().toLowerCase().contains(q)).toList();
        }
        if (status != null) {
            bookings = bookings.stream().filter(b -> b.getStatus() == status).toList();
        }
        if (date != null) {
            bookings = bookings.stream().filter(b -> !date.isBefore(b.getStartDate()) && !date.isAfter(b.getEndDate())).toList();
        }

        model.addAttribute("active", "bookings");
        model.addAttribute("bookings", bookings);
        model.addAttribute("customer", customer);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("status", status);
        model.addAttribute("date", date);
        model.addAttribute("statuses", BookingStatus.values());
        return "admin/bookings/list";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam BookingStatus status, RedirectAttributes redirectAttributes) {
        rentalBookingService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Booking status updated.");
        return "redirect:/admin/bookings";
    }
}
