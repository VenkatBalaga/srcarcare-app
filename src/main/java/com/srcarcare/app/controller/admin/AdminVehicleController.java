package com.srcarcare.app.controller.admin;

import com.srcarcare.app.entity.Vehicle;
import com.srcarcare.app.service.FileStorageService;
import com.srcarcare.app.service.VehicleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/vehicles")
public class AdminVehicleController {

    private final VehicleService vehicleService;
    private final FileStorageService fileStorageService;

    public AdminVehicleController(VehicleService vehicleService, FileStorageService fileStorageService) {
        this.vehicleService = vehicleService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "vehicles");
        model.addAttribute("vehicles", vehicleService.listAll());
        return "admin/vehicles/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("active", "vehicles");
        model.addAttribute("vehicle", new Vehicle());
        return "admin/vehicles/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("active", "vehicles");
        model.addAttribute("vehicle", vehicleService.getById(id));
        model.addAttribute("blockedDates", vehicleService.getBlockedDates(id));
        return "admin/vehicles/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                        @RequestParam String name,
                        @RequestParam(required = false) String description,
                        @RequestParam Integer seatingCapacity,
                        @RequestParam(required = false) Boolean selfDriveAvailable,
                        @RequestParam(required = false) Boolean withDriverAvailable,
                        @RequestParam BigDecimal selfDrivePrice,
                        @RequestParam BigDecimal withDriverPrice,
                        @RequestParam(required = false) Boolean active,
                        @RequestParam(required = false) Boolean available,
                        @RequestParam(required = false) MultipartFile image,
                        RedirectAttributes redirectAttributes) {
        Vehicle vehicle = id != null ? vehicleService.getById(id) : new Vehicle();
        vehicle.setName(name);
        vehicle.setDescription(description);
        vehicle.setSeatingCapacity(seatingCapacity);
        vehicle.setSelfDriveAvailable(Boolean.TRUE.equals(selfDriveAvailable));
        vehicle.setWithDriverAvailable(Boolean.TRUE.equals(withDriverAvailable));
        vehicle.setSelfDrivePrice(selfDrivePrice);
        vehicle.setWithDriverPrice(withDriverPrice);
        vehicle.setActive(Boolean.TRUE.equals(active));
        vehicle.setAvailable(Boolean.TRUE.equals(available));

        if (image != null && !image.isEmpty()) {
            String path = fileStorageService.store(image, "vehicles");
            vehicle.setImagePath(path);
        }

        vehicleService.save(vehicle);
        redirectAttributes.addFlashAttribute("success", "Vehicle saved successfully.");
        return "redirect:/admin/vehicles";
    }

    @PostMapping("/{id}/activate")
    public String activate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehicleService.activate(id);
        redirectAttributes.addFlashAttribute("success", "Vehicle activated.");
        return "redirect:/admin/vehicles";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehicleService.deactivate(id);
        redirectAttributes.addFlashAttribute("success", "Vehicle deactivated.");
        return "redirect:/admin/vehicles";
    }

    @PostMapping("/{id}/availability")
    public String toggleAvailability(@PathVariable Long id, @RequestParam boolean available, RedirectAttributes redirectAttributes) {
        vehicleService.setAvailable(id, available);
        redirectAttributes.addFlashAttribute("success", "Vehicle availability updated.");
        return "redirect:/admin/vehicles/" + id + "/edit";
    }

    @PostMapping("/{id}/block-dates")
    public String blockDates(@PathVariable Long id,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                              @RequestParam(required = false) String reason,
                              RedirectAttributes redirectAttributes) {
        if (endDate.isBefore(startDate)) {
            redirectAttributes.addFlashAttribute("error", "End date must be on or after start date.");
        } else {
            vehicleService.blockDates(id, startDate, endDate, reason);
            redirectAttributes.addFlashAttribute("success", "Vehicle blocked for the selected dates.");
        }
        return "redirect:/admin/vehicles/" + id + "/edit";
    }

    @PostMapping("/{id}/blocked-dates/{blockedDateId}/remove")
    public String removeBlockedDate(@PathVariable Long id, @PathVariable Long blockedDateId, RedirectAttributes redirectAttributes) {
        vehicleService.removeBlockedDate(blockedDateId);
        redirectAttributes.addFlashAttribute("success", "Blocked date removed.");
        return "redirect:/admin/vehicles/" + id + "/edit";
    }
}
