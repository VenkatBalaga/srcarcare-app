package com.srcarcare.app.service;

import com.srcarcare.app.entity.*;
import com.srcarcare.app.repository.VehicleRepository;
import com.srcarcare.app.util.BookingConflictException;
import com.srcarcare.app.util.InvalidPromoCodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class RentalBookingServiceTest {

    @Autowired
    private RentalBookingService rentalBookingService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private PromoCodeService promoCodeService;

    @Autowired
    private VehicleService vehicleService;

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setName("Test Sedan");
        vehicle.setSeatingCapacity(5);
        vehicle.setSelfDriveAvailable(true);
        vehicle.setWithDriverAvailable(false);
        vehicle.setSelfDrivePrice(new BigDecimal("1000"));
        vehicle.setWithDriverPrice(BigDecimal.ZERO);
        vehicle.setActive(true);
        vehicle.setAvailable(true);
        vehicle = vehicleRepository.save(vehicle);
    }

    @Test
    void createsBookingWithCorrectTotal() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3); // 3 days inclusive

        RentalBooking booking = rentalBookingService.createBooking(vehicle.getId(), "Alice", "9000000000",
                "alice@example.com", RentalType.SELF_DRIVE, start, end, null);

        assertEquals(new BigDecimal("3000"), booking.getBasePrice());
        assertEquals(BigDecimal.ZERO, booking.getDiscountAmount());
        assertEquals(new BigDecimal("3000"), booking.getTotalPrice());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
    }

    @Test
    void rejectsOverlappingBookingForSameVehicle() {
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate end = LocalDate.now().plusDays(7);
        rentalBookingService.createBooking(vehicle.getId(), "Alice", "9000000000", null,
                RentalType.SELF_DRIVE, start, end, null);

        LocalDate overlapStart = LocalDate.now().plusDays(6);
        LocalDate overlapEnd = LocalDate.now().plusDays(8);

        assertThrows(BookingConflictException.class, () ->
                rentalBookingService.createBooking(vehicle.getId(), "Bob", "9111111111", null,
                        RentalType.SELF_DRIVE, overlapStart, overlapEnd, null));
    }

    @Test
    void rejectsUnsupportedRentalType() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(2);

        assertThrows(BookingConflictException.class, () ->
                rentalBookingService.createBooking(vehicle.getId(), "Carol", "9222222222", null,
                        RentalType.WITH_DRIVER, start, end, null));
    }

    @Test
    void appliesValidPercentagePromoCode() {
        PromoCode promo = new PromoCode();
        promo.setCode("SAVE10");
        promo.setDiscountType(DiscountType.PERCENTAGE);
        promo.setDiscountValue(new BigDecimal("10"));
        promo.setValidFrom(LocalDate.now().minusDays(1));
        promo.setValidTo(LocalDate.now().plusDays(30));
        promo.setUsageCount(0);
        promo.setActive(true);
        promoCodeService.save(promo);

        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(2); // 2 days = 2000

        RentalBooking booking = rentalBookingService.createBooking(vehicle.getId(), "Dan", "9333333333", null,
                RentalType.SELF_DRIVE, start, end, "save10");

        assertEquals(new BigDecimal("2000"), booking.getBasePrice());
        assertEquals(new BigDecimal("200.00"), booking.getDiscountAmount());
        assertEquals(new BigDecimal("1800.00"), booking.getTotalPrice());
    }

    @Test
    void rejectsExpiredPromoCode() {
        PromoCode promo = new PromoCode();
        promo.setCode("OLD5");
        promo.setDiscountType(DiscountType.FIXED);
        promo.setDiscountValue(new BigDecimal("50"));
        promo.setValidFrom(LocalDate.now().minusDays(30));
        promo.setValidTo(LocalDate.now().minusDays(1));
        promo.setUsageCount(0);
        promo.setActive(true);
        promoCodeService.save(promo);

        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(2);

        assertThrows(InvalidPromoCodeException.class, () ->
                rentalBookingService.createBooking(vehicle.getId(), "Eve", "9444444444", null,
                        RentalType.SELF_DRIVE, start, end, "OLD5"));
    }

    @Test
    void blockedDatesPreventBooking() {
        LocalDate blockStart = LocalDate.now().plusDays(10);
        LocalDate blockEnd = LocalDate.now().plusDays(12);
        vehicleService.blockDates(vehicle.getId(), blockStart, blockEnd, "Maintenance");

        assertThrows(BookingConflictException.class, () ->
                rentalBookingService.createBooking(vehicle.getId(), "Frank", "9555555555", null,
                        RentalType.SELF_DRIVE, blockStart.plusDays(1), blockEnd.minusDays(1), null));
    }
}
