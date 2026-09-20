package com.srcarcare.app.controller.admin;

import com.srcarcare.app.entity.Offer;
import com.srcarcare.app.service.FileStorageService;
import com.srcarcare.app.service.OfferService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/offers")
public class AdminOfferController {

    private final OfferService offerService;
    private final FileStorageService fileStorageService;

    public AdminOfferController(OfferService offerService, FileStorageService fileStorageService) {
        this.offerService = offerService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "offers");
        model.addAttribute("offers", offerService.listAll());
        return "admin/offers/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("active", "offers");
        model.addAttribute("offer", new Offer());
        return "admin/offers/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("active", "offers");
        model.addAttribute("offer", offerService.getById(id));
        return "admin/offers/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                        @RequestParam String title,
                        @RequestParam(required = false) String description,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                        @RequestParam(required = false) Boolean active,
                        @RequestParam(required = false) MultipartFile image,
                        RedirectAttributes redirectAttributes) {
        Offer offer = id != null ? offerService.getById(id) : new Offer();
        offer.setTitle(title);
        offer.setDescription(description);
        offer.setStartDate(startDate);
        offer.setEndDate(endDate);
        offer.setActive(Boolean.TRUE.equals(active));
        if (image != null && !image.isEmpty()) {
            offer.setImagePath(fileStorageService.store(image, "offers"));
        }
        offerService.save(offer);
        redirectAttributes.addFlashAttribute("success", "Offer saved.");
        return "redirect:/admin/offers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        offerService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Offer deleted.");
        return "redirect:/admin/offers";
    }
}
