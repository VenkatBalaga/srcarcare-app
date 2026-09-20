package com.srcarcare.app.service;

import com.srcarcare.app.entity.*;
import com.srcarcare.app.repository.RentalBookingRepository;
import com.srcarcare.app.util.BookingConflictException;
import com.srcarcare.app.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RentalBookingService {

    private final RentalBookingRepository rentalBookingRepository;
    private final VehicleService vehicleService;
    private final PromoCodeService promoCodeService;

    public RentalBookingService(RentalBookingRepository rentalBookingRepository,
                                 VehicleService vehicleService,
                                 PromoCodeService promoCodeService) {
        this.rentalBookingRepository = rentalBookingRepository;
        this.vehicleService = vehicleService;
        this.promoCodeService = promoCodeService;
    }

    @Transactional
    public RentalBooking createBooking(Long vehicleId, String customerName, String customerPhone, String customerEmail,
                                        RentalType rentalType, LocalDate startDate, LocalDate endDate, String promoCode) {

        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Please provide a valid date range.");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past.");
        }

        Vehicle vehicle = vehicleService.getById(vehicleId);
        if (!vehicle.isActive() || !vehicle.isAvailable()) {
            throw new BookingConflictException("This vehicle is currently not available for booking.");
        }
        if (rentalType == RentalType.SELF_DRIVE && !vehicle.isSelfDriveAvailable()) {
            throw new BookingConflictException("Self-drive is not available for this vehicle.");
        }
        if (rentalType == RentalType.WITH_DRIVER && !vehicle.isWithDriverAvailable()) {
            throw new BookingConflictException("With-driver rental is not available for this vehicle.");
        }
        if (!vehicleService.isVehicleFreeForDates(vehicleId, startDate, endDate)) {
            throw new BookingConflictException("This vehicle is already booked or blocked for the selected dates.");
        }

        long days = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate) + 1);
        BigDecimal dailyRate = rentalType == RentalType.SELF_DRIVE ? vehicle.getSelfDrivePrice() : vehicle.getWithDriverPrice();
        BigDecimal basePrice = dailyRate.multiply(BigDecimal.valueOf(days));

        BigDecimal discount = BigDecimal.ZERO;
        String appliedPromoCode = null;
        if (promoCode != null && !promoCode.isBlank()) {
            discount = promoCodeService.validateAndCalculateDiscount(promoCode, basePrice);
            appliedPromoCode = promoCode.trim().toUpperCase();
        }

        RentalBooking booking = new RentalBooking();
        booking.setVehicle(vehicle);
        booking.setCustomerName(customerName);
        booking.setCustomerPhone(customerPhone);
        booking.setCustomerEmail(customerEmail);
        booking.setRentalType(rentalType);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setBasePrice(basePrice);
        booking.setDiscountAmount(discount);
        booking.setTotalPrice(basePrice.subtract(discount));
        booking.setPromoCode(appliedPromoCode);
        booking.setStatus(BookingStatus.PENDING);

        RentalBooking saved = rentalBookingRepository.save(booking);

        if (appliedPromoCode != null) {
            promoCodeService.recordUsage(appliedPromoCode);
        }
        return saved;
    }

    public List<RentalBooking> listAll() {
        return rentalBookingRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<RentalBooking> upcoming() {
        return rentalBookingRepository.findUpcoming(LocalDate.now());
    }

    public List<RentalBooking> recent(int limit) {
        return rentalBookingRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public RentalBooking getById(Long id) {
        return rentalBookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id));
    }

    public RentalBooking updateStatus(Long id, BookingStatus status) {
        RentalBooking booking = getById(id);
        booking.setStatus(status);
        return rentalBookingRepository.save(booking);
    }

    public long countByStatus(BookingStatus status) {
        return rentalBookingRepository.countByStatus(status);
    }

    public long countAll() {
        return rentalBookingRepository.count();
    }
}
