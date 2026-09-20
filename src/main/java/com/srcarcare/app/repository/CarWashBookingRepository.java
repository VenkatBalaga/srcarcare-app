package com.srcarcare.app.repository;

import com.srcarcare.app.entity.BookingStatus;
import com.srcarcare.app.entity.CarWashBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CarWashBookingRepository extends JpaRepository<CarWashBooking, Long> {
    List<CarWashBooking> findAllByOrderByCreatedAtDesc();
    List<CarWashBooking> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    long countByStatus(BookingStatus status);
    List<CarWashBooking> findTop10ByOrderByCreatedAtDesc();
}
