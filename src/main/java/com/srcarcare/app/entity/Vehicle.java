package com.srcarcare.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "image_path")
    private String imagePath;

    @NotNull
    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity; // 5 or 7

    @Column(name = "self_drive_available", nullable = false)
    private boolean selfDriveAvailable = true;

    @Column(name = "with_driver_available", nullable = false)
    private boolean withDriverAvailable = true;

    @Column(name = "self_drive_price", precision = 10, scale = 2)
    private BigDecimal selfDrivePrice = BigDecimal.ZERO;

    @Column(name = "with_driver_price", precision = 10, scale = 2)
    private BigDecimal withDriverPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean available = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VehicleBlockedDate> blockedDates = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public Integer getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(Integer seatingCapacity) { this.seatingCapacity = seatingCapacity; }
    public boolean isSelfDriveAvailable() { return selfDriveAvailable; }
    public void setSelfDriveAvailable(boolean selfDriveAvailable) { this.selfDriveAvailable = selfDriveAvailable; }
    public boolean isWithDriverAvailable() { return withDriverAvailable; }
    public void setWithDriverAvailable(boolean withDriverAvailable) { this.withDriverAvailable = withDriverAvailable; }
    public BigDecimal getSelfDrivePrice() { return selfDrivePrice; }
    public void setSelfDrivePrice(BigDecimal selfDrivePrice) { this.selfDrivePrice = selfDrivePrice; }
    public BigDecimal getWithDriverPrice() { return withDriverPrice; }
    public void setWithDriverPrice(BigDecimal withDriverPrice) { this.withDriverPrice = withDriverPrice; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<VehicleBlockedDate> getBlockedDates() { return blockedDates; }
    public void setBlockedDates(List<VehicleBlockedDate> blockedDates) { this.blockedDates = blockedDates; }
}
