package com.srcarcare.app.service;

import com.srcarcare.app.entity.BookingStatus;
import com.srcarcare.app.entity.CarWashBooking;
import com.srcarcare.app.entity.CarWashOption;
import com.srcarcare.app.repository.CarWashBookingRepository;
import com.srcarcare.app.repository.CarWashOptionRepository;
import com.srcarcare.app.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CarWashService {

    private final CarWashOptionRepository optionRepository;
    private final CarWashBookingRepository bookingRepository;

    public CarWashService(CarWashOptionRepository optionRepository, CarWashBookingRepository bookingRepository) {
        this.optionRepository = optionRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<CarWashOption> listActiveOptions() {
        return optionRepository.findByActiveTrue();
    }

    public List<CarWashOption> listAllOptions() {
        return optionRepository.findAllByOrderByIdDesc();
    }

    public CarWashOption getOption(Long id) {
        return optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car wash option not found with id " + id));
    }

    public CarWashOption saveOption(CarWashOption option) {
        return optionRepository.save(option);
    }

    public void deleteOption(Long id) {
        optionRepository.deleteById(id);
    }

    public CarWashBooking createBooking(Long optionId, String name, String phone, String email,
                                         String vehicleInfo, LocalDate preferredDate, String address) {
        if (preferredDate == null || preferredDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Please choose a valid preferred date (today or later).");
        }
        CarWashOption option = getOption(optionId);
        if (!option.isActive()) {
            throw new IllegalArgumentException("This wash option is currently unavailable.");
        }
        CarWashBooking booking = new CarWashBooking();
        booking.setOption(option);
        booking.setCustomerName(name);
        booking.setCustomerPhone(phone);
        booking.setCustomerEmail(email);
        booking.setVehicleInfo(vehicleInfo);
        booking.setPreferredDate(preferredDate);
        booking.setAddress(address);
        booking.setStatus(BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public List<CarWashBooking> listAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    public CarWashBooking getBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car wash booking not found with id " + id));
    }

    public CarWashBooking updateStatus(Long id, BookingStatus status) {
        CarWashBooking booking = getBooking(id);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public long countByStatus(BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }

    public long countAll() {
        return bookingRepository.count();
    }

    public List<CarWashBooking> recent() {
        return bookingRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
