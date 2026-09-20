package com.srcarcare.app.service;

import com.srcarcare.app.entity.RentalType;
import com.srcarcare.app.entity.Vehicle;
import com.srcarcare.app.entity.VehicleBlockedDate;
import com.srcarcare.app.repository.RentalBookingRepository;
import com.srcarcare.app.repository.VehicleBlockedDateRepository;
import com.srcarcare.app.repository.VehicleRepository;
import com.srcarcare.app.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleBlockedDateRepository blockedDateRepository;
    private final RentalBookingRepository rentalBookingRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                           VehicleBlockedDateRepository blockedDateRepository,
                           RentalBookingRepository rentalBookingRepository) {
        this.vehicleRepository = vehicleRepository;
        this.blockedDateRepository = blockedDateRepository;
        this.rentalBookingRepository = rentalBookingRepository;
    }

    public List<Vehicle> listActive() {
        return vehicleRepository.findByActiveTrueOrderByIdDesc();
    }

    public List<Vehicle> listAll() {
        return vehicleRepository.findAllByOrderByIdDesc();
    }

    public Vehicle getById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id " + id));
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void deactivate(Long id) {
        Vehicle vehicle = getById(id);
        vehicle.setActive(false);
        vehicleRepository.save(vehicle);
    }

    public void activate(Long id) {
        Vehicle vehicle = getById(id);
        vehicle.setActive(true);
        vehicleRepository.save(vehicle);
    }

    public void setAvailable(Long id, boolean available) {
        Vehicle vehicle = getById(id);
        vehicle.setAvailable(available);
        vehicleRepository.save(vehicle);
    }

    public List<Vehicle> searchAvailable(Integer seatingCapacity, RentalType rentalType, LocalDate startDate, LocalDate endDate) {
        List<Vehicle> candidates = seatingCapacity != null
                ? vehicleRepository.findBySeatingCapacityAndActiveTrue(seatingCapacity)
                : vehicleRepository.findByActiveTrueOrderByIdDesc();

        return candidates.stream()
                .filter(Vehicle::isAvailable)
                .filter(v -> rentalType == null
                        || (rentalType == RentalType.SELF_DRIVE && v.isSelfDriveAvailable())
                        || (rentalType == RentalType.WITH_DRIVER && v.isWithDriverAvailable()))
                .filter(v -> startDate == null || endDate == null || isVehicleFreeForDates(v.getId(), startDate, endDate))
                .collect(Collectors.toList());
    }

    public boolean isVehicleFreeForDates(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        boolean blocked = blockedDateRepository.findByVehicleId(vehicleId).stream()
                .anyMatch(b -> !startDate.isAfter(b.getEndDate()) && !endDate.isBefore(b.getStartDate()));
        if (blocked) {
            return false;
        }
        return rentalBookingRepository.findConflictingBookings(vehicleId, startDate, endDate).isEmpty();
    }

    public List<VehicleBlockedDate> getBlockedDates(Long vehicleId) {
        return blockedDateRepository.findByVehicleId(vehicleId);
    }

    public VehicleBlockedDate blockDates(Long vehicleId, LocalDate startDate, LocalDate endDate, String reason) {
        Vehicle vehicle = getById(vehicleId);
        VehicleBlockedDate blocked = new VehicleBlockedDate();
        blocked.setVehicle(vehicle);
        blocked.setStartDate(startDate);
        blocked.setEndDate(endDate);
        blocked.setReason(reason);
        return blockedDateRepository.save(blocked);
    }

    public void removeBlockedDate(Long blockedDateId) {
        blockedDateRepository.deleteById(blockedDateId);
    }
}
