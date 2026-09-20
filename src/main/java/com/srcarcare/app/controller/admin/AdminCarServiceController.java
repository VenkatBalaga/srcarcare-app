package com.srcarcare.app.controller.admin;

import com.srcarcare.app.entity.BookingStatus;
import com.srcarcare.app.entity.CarServiceOption;
import com.srcarcare.app.service.CarServiceBookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/car-service")
public class AdminCarServiceController {

    private final CarServiceBookingService carServiceBookingService;

    public AdminCarServiceController(CarServiceBookingService carServiceBookingService) {
        this.carServiceBookingService = carServiceBookingService;
    }

    @GetMapping("/options")
    public String options(Model model) {
        model.addAttribute("active", "service-options");
        model.addAttribute("options", carServiceBookingService.listAllOptions());
        return "admin/service/options";
    }

    @PostMapping("/options/save")
    public String saveOption(@RequestParam(required = false) Long id,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam BigDecimal price,
                              @RequestParam(required = false) Boolean active,
                              RedirectAttributes redirectAttributes) {
        CarServiceOption option = id != null ? carServiceBookingService.getOption(id) : new CarServiceOption();
        option.setName(name);
        option.setDescription(description);
        option.setPrice(price);
        option.setActive(Boolean.TRUE.equals(active));
        carServiceBookingService.saveOption(option);
        redirectAttributes.addFlashAttribute("success", "Service option saved.");
        return "redirect:/admin/car-service/options";
    }

    @PostMapping("/options/{id}/delete")
    public String deleteOption(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        carServiceBookingService.deleteOption(id);
        redirectAttributes.addFlashAttribute("success", "Service option deleted.");
        return "redirect:/admin/car-service/options";
    }

    @GetMapping("/requests")
    public String requests(Model model) {
        model.addAttribute("active", "service-requests");
        model.addAttribute("bookings", carServiceBookingService.listAllBookings());
        model.addAttribute("statuses", BookingStatus.values());
        return "admin/service/requests";
    }

    @PostMapping("/requests/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam BookingStatus status, RedirectAttributes redirectAttributes) {
        carServiceBookingService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Request status updated.");
        return "redirect:/admin/car-service/requests";
    }
}
