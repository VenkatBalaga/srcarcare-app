package com.srcarcare.app.service;

import com.srcarcare.app.entity.BookingStatus;
import com.srcarcare.app.entity.CarServiceBooking;
import com.srcarcare.app.entity.CarServiceOption;
import com.srcarcare.app.repository.CarServiceBookingRepository;
import com.srcarcare.app.repository.CarServiceOptionRepository;
import com.srcarcare.app.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarServiceBookingService {

    private final CarServiceOptionRepository optionRepository;
    private final CarServiceBookingRepository bookingRepository;

    public CarServiceBookingService(CarServiceOptionRepository optionRepository, CarServiceBookingRepository bookingRepository) {
        this.optionRepository = optionRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<CarServiceOption> listActiveOptions() {
        return optionRepository.findByActiveTrue();
    }

    public List<CarServiceOption> listAllOptions() {
        return optionRepository.findAllByOrderByIdDesc();
    }

    public CarServiceOption getOption(Long id) {
        return optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car service option not found with id " + id));
    }

    public CarServiceOption saveOption(CarServiceOption option) {
        return optionRepository.save(option);
    }

    public void deleteOption(Long id) {
        optionRepository.deleteById(id);
    }

    public CarServiceBooking createBooking(Long optionId, String name, String phone, String email,
                                            String vehicleInfo, LocalDate preferredDate, String issueDescription) {
        if (preferredDate == null || preferredDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Please choose a valid preferred date (today or later).");
        }
        CarServiceOption option = getOption(optionId);
        if (!option.isActive()) {
            throw new IllegalArgumentException("This service option is currently unavailable.");
        }
        CarServiceBooking booking = new CarServiceBooking();
        booking.setOption(option);
        booking.setCustomerName(name);
        booking.setCustomerPhone(phone);
        booking.setCustomerEmail(email);
        booking.setVehicleInfo(vehicleInfo);
        booking.setPreferredDate(preferredDate);
        booking.setIssueDescription(issueDescription);
        booking.setStatus(BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public List<CarServiceBooking> listAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    public CarServiceBooking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car service booking not found with id " + id));
    }

    public CarServiceBooking updateStatus(Long id, BookingStatus status) {
        CarServiceBooking booking = getBooking(id);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public long countByStatus(BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }

    public long countAll() {
        return bookingRepository.count();
    }

    public List<CarServiceBooking> recent() {
        return bookingRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
