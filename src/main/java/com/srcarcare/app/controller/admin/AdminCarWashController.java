package com.srcarcare.app.controller.admin;

import com.srcarcare.app.entity.BookingStatus;
import com.srcarcare.app.entity.CarWashOption;
import com.srcarcare.app.service.CarWashService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/car-wash")
public class AdminCarWashController {

    private final CarWashService carWashService;

    public AdminCarWashController(CarWashService carWashService) {
        this.carWashService = carWashService;
    }

    @GetMapping("/options")
    public String options(Model model) {
        model.addAttribute("active", "wash-options");
        model.addAttribute("options", carWashService.listAllOptions());
        return "admin/wash/options";
    }

    @PostMapping("/options/save")
    public String saveOption(@RequestParam(required = false) Long id,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam BigDecimal price,
                              @RequestParam(required = false) Boolean active,
                              RedirectAttributes redirectAttributes) {
        CarWashOption option = id != null ? carWashService.getOption(id) : new CarWashOption();
        option.setName(name);
        option.setDescription(description);
        option.setPrice(price);
        option.setActive(Boolean.TRUE.equals(active));
        carWashService.saveOption(option);
        redirectAttributes.addFlashAttribute("success", "Wash option saved.");
        return "redirect:/admin/car-wash/options";
    }

    @PostMapping("/options/{id}/delete")
    public String deleteOption(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        carWashService.deleteOption(id);
        redirectAttributes.addFlashAttribute("success", "Wash option deleted.");
        return "redirect:/admin/car-wash/options";
    }

    @GetMapping("/requests")
    public String requests(Model model) {
        model.addAttribute("active", "wash-requests");
        model.addAttribute("bookings", carWashService.listAllBookings());
        model.addAttribute("statuses", BookingStatus.values());
        return "admin/wash/requests";
    }

    @PostMapping("/requests/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam BookingStatus status, RedirectAttributes redirectAttributes) {
        carWashService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Request status updated.");
        return "redirect:/admin/car-wash/requests";
    }
}
