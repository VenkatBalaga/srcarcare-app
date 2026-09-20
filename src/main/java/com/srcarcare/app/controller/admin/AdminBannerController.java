package com.srcarcare.app.controller.admin;

import com.srcarcare.app.entity.Banner;
import com.srcarcare.app.service.BannerService;
import com.srcarcare.app.service.FileStorageService;
import com.srcarcare.app.util.InvalidUploadException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/banners")
public class AdminBannerController {

    private final BannerService bannerService;
    private final FileStorageService fileStorageService;

    public AdminBannerController(BannerService bannerService, FileStorageService fileStorageService) {
        this.bannerService = bannerService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "banners");
        model.addAttribute("banners", bannerService.listAll());
        return "admin/banners/list";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                        @RequestParam String title,
                        @RequestParam(required = false) String subtitle,
                        @RequestParam(required = false) String linkUrl,
                        @RequestParam(required = false) Integer displayOrder,
                        @RequestParam(required = false) Boolean active,
                        @RequestParam(required = false) MultipartFile image,
                        RedirectAttributes redirectAttributes) {
        Banner banner = id != null ? bannerService.getById(id) : new Banner();
        banner.setTitle(title);
        banner.setSubtitle(subtitle);
        banner.setLinkUrl(linkUrl);
        banner.setDisplayOrder(displayOrder != null ? displayOrder : 0);
        banner.setActive(Boolean.TRUE.equals(active));

        if (image != null && !image.isEmpty()) {
            banner.setImagePath(fileStorageService.store(image, "banners"));
        }
        if (banner.getImagePath() == null) {
            redirectAttributes.addFlashAttribute("error", "Please upload a banner image.");
            return "redirect:/admin/banners";
        }
        try {
            bannerService.save(banner);
        } catch (InvalidUploadException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/banners";
        }
        redirectAttributes.addFlashAttribute("success", "Banner saved.");
        return "redirect:/admin/banners";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bannerService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Banner deleted.");
        return "redirect:/admin/banners";
    }
}
