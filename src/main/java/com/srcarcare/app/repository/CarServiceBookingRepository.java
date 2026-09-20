package com.srcarcare.app.repository;

import com.srcarcare.app.entity.BookingStatus;
import com.srcarcare.app.entity.CarServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CarServiceBookingRepository extends JpaRepository<CarServiceBooking, Long> {
    List<CarServiceBooking> findAllByOrderByCreatedAtDesc();
    List<CarServiceBooking> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
    long countByStatus(BookingStatus status);
    List<CarServiceBooking> findTop10ByOrderByCreatedAtDesc();
}
