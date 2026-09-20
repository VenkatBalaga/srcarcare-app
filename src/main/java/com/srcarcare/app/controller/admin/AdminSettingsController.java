package com.srcarcare.app.controller.admin;

import com.srcarcare.app.entity.BusinessSettings;
import com.srcarcare.app.service.BusinessSettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/settings")
public class AdminSettingsController {

    private final BusinessSettingsService businessSettingsService;

    public AdminSettingsController(BusinessSettingsService businessSettingsService) {
        this.businessSettingsService = businessSettingsService;
    }

    @GetMapping
    public String settings(Model model) {
        model.addAttribute("active", "settings");
        model.addAttribute("settings", businessSettingsService.get());
        return "admin/settings";
    }

    @PostMapping("/save")
    public String save(@RequestParam String businessName,
                        @RequestParam String ownerName,
                        @RequestParam String phoneNumber,
                        @RequestParam String whatsappNumber,
                        @RequestParam String email,
                        @RequestParam String address,
                        @RequestParam String workingHours,
                        @RequestParam(required = false) Boolean phonepeEnabled,
                        @RequestParam(required = false) String phonepeMerchantId,
                        RedirectAttributes redirectAttributes) {
        BusinessSettings settings = businessSettingsService.get();
        settings.setBusinessName(businessName);
        settings.setOwnerName(ownerName);
        settings.setPhoneNumber(phoneNumber);
        settings.setWhatsappNumber(whatsappNumber);
        settings.setEmail(email);
        settings.setAddress(address);
        settings.setWorkingHours(workingHours);
        settings.setPhonepeEnabled(Boolean.TRUE.equals(phonepeEnabled));
        settings.setPhonepeMerchantId(phonepeMerchantId);
        businessSettingsService.save(settings);
        redirectAttributes.addFlashAttribute("success", "Business settings updated.");
        return "redirect:/admin/settings";
    }
}
