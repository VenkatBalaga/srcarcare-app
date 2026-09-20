package com.srcarcare.app.controller.admin;

import com.srcarcare.app.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "reviews");
        model.addAttribute("reviews", reviewService.listAll());
        return "admin/reviews/list";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.approve(id);
        redirectAttributes.addFlashAttribute("success", "Review approved and published.");
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/hide")
    public String hide(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.hide(id);
        redirectAttributes.addFlashAttribute("success", "Review hidden from the public site.");
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Review deleted.");
        return "redirect:/admin/reviews";
    }
}
