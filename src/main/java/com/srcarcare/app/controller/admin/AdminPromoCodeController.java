package com.srcarcare.app.controller.admin;

import com.srcarcare.app.entity.DiscountType;
import com.srcarcare.app.entity.PromoCode;
import com.srcarcare.app.service.PromoCodeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/promo-codes")
public class AdminPromoCodeController {

    private final PromoCodeService promoCodeService;

    public AdminPromoCodeController(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("active", "promo-codes");
        model.addAttribute("promoCodes", promoCodeService.listAll());
        model.addAttribute("discountTypes", DiscountType.values());
        return "admin/promocodes/list";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                        @RequestParam String code,
                        @RequestParam DiscountType discountType,
                        @RequestParam BigDecimal discountValue,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate validFrom,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate validTo,
                        @RequestParam(required = false) Integer usageLimit,
                        @RequestParam(required = false) Boolean active,
                        RedirectAttributes redirectAttributes) {
        PromoCode promo = id != null ? promoCodeService.getById(id) : new PromoCode();
        promo.setCode(code);
        promo.setDiscountType(discountType);
        promo.setDiscountValue(discountValue);
        promo.setValidFrom(validFrom);
        promo.setValidTo(validTo);
        promo.setUsageLimit(usageLimit);
        promo.setActive(Boolean.TRUE.equals(active));
        if (promo.getUsageCount() == null) {
            promo.setUsageCount(0);
        }
        promoCodeService.save(promo);
        redirectAttributes.addFlashAttribute("success", "Promo code saved.");
        return "redirect:/admin/promo-codes";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        promoCodeService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Promo code deleted.");
        return "redirect:/admin/promo-codes";
    }
}
