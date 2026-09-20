package com.srcarcare.app.service;

import com.srcarcare.app.entity.DiscountType;
import com.srcarcare.app.entity.PromoCode;
import com.srcarcare.app.repository.PromoCodeRepository;
import com.srcarcare.app.util.InvalidPromoCodeException;
import com.srcarcare.app.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    public PromoCodeService(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    public List<PromoCode> listAll() {
        return promoCodeRepository.findAllByOrderByIdDesc();
    }

    public PromoCode getById(Long id) {
        return promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found with id " + id));
    }

    public PromoCode save(PromoCode promoCode) {
        promoCode.setCode(promoCode.getCode().trim().toUpperCase());
        return promoCodeRepository.save(promoCode);
    }

    public void delete(Long id) {
        promoCodeRepository.deleteById(id);
    }

    /**
     * Validates a promo code against the current date and usage limits, and returns
     * the discount amount to apply on the given base amount. Throws InvalidPromoCodeException
     * if the code is not usable.
     */
    public BigDecimal validateAndCalculateDiscount(String code, BigDecimal baseAmount) {
        if (code == null || code.isBlank()) {
            throw new InvalidPromoCodeException("Promo code is required.");
        }
        PromoCode promo = promoCodeRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new InvalidPromoCodeException("Promo code \"" + code + "\" does not exist."));

        if (!promo.isActive()) {
            throw new InvalidPromoCodeException("Promo code \"" + code + "\" is no longer active.");
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(promo.getValidFrom()) || today.isAfter(promo.getValidTo())) {
            throw new InvalidPromoCodeException("Promo code \"" + code + "\" is not valid today.");
        }
        if (promo.getUsageLimit() != null && promo.getUsageCount() >= promo.getUsageLimit()) {
            throw new InvalidPromoCodeException("Promo code \"" + code + "\" has reached its usage limit.");
        }

        BigDecimal discount;
        if (promo.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = baseAmount.multiply(promo.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = promo.getDiscountValue();
        }
        if (discount.compareTo(baseAmount) > 0) {
            discount = baseAmount;
        }
        return discount;
    }

    public void recordUsage(String code) {
        promoCodeRepository.findByCodeIgnoreCase(code.trim()).ifPresent(promo -> {
            promo.setUsageCount(promo.getUsageCount() + 1);
            promoCodeRepository.save(promo);
        });
    }
}
