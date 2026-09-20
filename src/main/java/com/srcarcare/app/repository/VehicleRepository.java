package com.srcarcare.app.repository;

import com.srcarcare.app.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByActiveTrueOrderByIdDesc();
    List<Vehicle> findAllByOrderByIdDesc();
    List<Vehicle> findBySeatingCapacityAndActiveTrue(Integer seatingCapacity);
}
