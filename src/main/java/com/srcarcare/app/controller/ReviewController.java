package com.srcarcare.app.controller;

import com.srcarcare.app.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("active", "reviews");
        model.addAttribute("reviews", reviewService.listApproved());
        return "reviews";
    }

    @PostMapping("/submit")
    public String submit(@RequestParam String customerName,
                          @RequestParam Integer rating,
                          @RequestParam(required = false) String reviewText,
                          @RequestParam(required = false) List<MultipartFile> photos,
                          RedirectAttributes redirectAttributes) {
        try {
            if (rating == null || rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Please provide a rating between 1 and 5.");
            }
            reviewService.submit(customerName, rating, reviewText, photos);
            redirectAttributes.addFlashAttribute("success", "Thank you for your review! It will appear after admin approval.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reviews";
    }
}
