package com.srcarcare.app.repository;

import com.srcarcare.app.entity.BookingStatus;
import com.srcarcare.app.entity.RentalBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RentalBookingRepository extends JpaRepository<RentalBooking, Long> {

    List<RentalBooking> findAllByOrderByCreatedAtDesc();

    @Query("select b from RentalBooking b where b.vehicle.id = :vehicleId " +
           "and b.status <> com.srcarcare.app.entity.BookingStatus.CANCELLED " +
           "and b.startDate <= :endDate and b.endDate >= :startDate")
    List<RentalBooking> findConflictingBookings(@Param("vehicleId") Long vehicleId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    List<RentalBooking> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    List<RentalBooking> findByStatus(BookingStatus status);

    long countByStatus(BookingStatus status);

    List<RentalBooking> findTop10ByOrderByCreatedAtDesc();

    @Query("select b from RentalBooking b where b.status in (com.srcarcare.app.entity.BookingStatus.PENDING, com.srcarcare.app.entity.BookingStatus.CONFIRMED) " +
           "and b.startDate >= :today order by b.startDate asc")
    List<RentalBooking> findUpcoming(@Param("today") LocalDate today);
}
