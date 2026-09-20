package com.srcarcare.app.repository;

import com.srcarcare.app.entity.VehicleBlockedDate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleBlockedDateRepository extends JpaRepository<VehicleBlockedDate, Long> {
    List<VehicleBlockedDate> findByVehicleId(Long vehicleId);
    List<VehicleBlockedDate> findByVehicleIdAndEndDateGreaterThanEqualOrderByStartDateAsc(Long vehicleId, java.time.LocalDate today);
}
