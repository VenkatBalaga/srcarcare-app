package com.srcarcare.app.config;

import com.srcarcare.app.entity.*;
import com.srcarcare.app.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final BusinessSettingsRepository businessSettingsRepository;
    private final VehicleRepository vehicleRepository;
    private final CarWashOptionRepository carWashOptionRepository;
    private final CarServiceOptionRepository carServiceOptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${srcarcare.admin.default-username}")
    private String defaultAdminUsername;

    @Value("${srcarcare.admin.default-password}")
    private String defaultAdminPassword;

    public DataInitializer(AdminUserRepository adminUserRepository,
                            BusinessSettingsRepository businessSettingsRepository,
                            VehicleRepository vehicleRepository,
                            CarWashOptionRepository carWashOptionRepository,
                            CarServiceOptionRepository carServiceOptionRepository,
                            PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.businessSettingsRepository = businessSettingsRepository;
        this.vehicleRepository = vehicleRepository;
        this.carWashOptionRepository = carWashOptionRepository;
        this.carServiceOptionRepository = carServiceOptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAdmin();
        seedBusinessSettings();
        seedVehicles();
        seedWashOptions();
        seedServiceOptions();
    }

    private void seedAdmin() {
        if (adminUserRepository.count() == 0) {
            AdminUser admin = new AdminUser();
            admin.setUsername(defaultAdminUsername);
            admin.setPassword(passwordEncoder.encode(defaultAdminPassword));
            admin.setEnabled(true);
            adminUserRepository.save(admin);
        }
    }

    private void seedBusinessSettings() {
        if (businessSettingsRepository.count() == 0) {
            BusinessSettings settings = new BusinessSettings();
            settings.setId(1L);
            settings.setBusinessName("SR CAR CARE");
            settings.setOwnerName("SR Car Care");
            settings.setPhoneNumber("+91 90000 00000");
            settings.setWhatsappNumber("+91 90000 00000");
            settings.setEmail("contact@srcarcare.in");
            settings.setAddress("SR Car Care, Main Road, Your City");
            settings.setWorkingHours("Mon - Sun: 8:00 AM - 8:00 PM");
            settings.setPhonepeEnabled(false);
            businessSettingsRepository.save(settings);
        }
    }

    private void seedVehicles() {
        if (vehicleRepository.count() == 0) {
            Vehicle v1 = new Vehicle();
            v1.setName("Swift Dzire");
            v1.setDescription("Comfortable 5-seater sedan, perfect for city drives and outstation trips.");
            v1.setSeatingCapacity(5);
            v1.setSelfDriveAvailable(true);
            v1.setWithDriverAvailable(true);
            v1.setSelfDrivePrice(new BigDecimal("1800"));
            v1.setWithDriverPrice(new BigDecimal("2500"));
            v1.setActive(true);
            v1.setAvailable(true);
            vehicleRepository.save(v1);

            Vehicle v2 = new Vehicle();
            v2.setName("Toyota Innova Crysta");
            v2.setDescription("Spacious 7-seater SUV, ideal for family trips and group travel.");
            v2.setSeatingCapacity(7);
            v2.setSelfDriveAvailable(false);
            v2.setWithDriverAvailable(true);
            v2.setSelfDrivePrice(BigDecimal.ZERO);
            v2.setWithDriverPrice(new BigDecimal("4200"));
            v2.setActive(true);
            v2.setAvailable(true);
            vehicleRepository.save(v2);
        }
    }

    private void seedWashOptions() {
        if (carWashOptionRepository.count() == 0) {
            CarWashOption basic = new CarWashOption();
            basic.setName("Basic Water Wash");
            basic.setDescription("Exterior water wash with foam shampoo and dry wipe.");
            basic.setPrice(new BigDecimal("299"));
            basic.setActive(true);
            carWashOptionRepository.save(basic);

            CarWashOption premium = new CarWashOption();
            premium.setName("Premium Wash & Interior Vacuum");
            premium.setDescription("Exterior wash plus interior vacuuming and dashboard polish.");
            premium.setPrice(new BigDecimal("599"));
            premium.setActive(true);
            carWashOptionRepository.save(premium);
        }
    }

    private void seedServiceOptions() {
        if (carServiceOptionRepository.count() == 0) {
            CarServiceOption general = new CarServiceOption();
            general.setName("General Service");
            general.setDescription("Oil change, filter check, brake inspection and general checkup.");
            general.setPrice(new BigDecimal("1499"));
            general.setActive(true);
            carServiceOptionRepository.save(general);

            CarServiceOption ac = new CarServiceOption();
            ac.setName("AC Service & Gas Refill");
            ac.setDescription("Complete AC checkup, cleaning and gas top-up.");
            ac.setPrice(new BigDecimal("1299"));
            ac.setActive(true);
            carServiceOptionRepository.save(ac);
        }
    }
}
