package com.srcarcare.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "business_settings")
public class BusinessSettings {

    @Id
    private Long id = 1L; // singleton row

    @Column(name = "business_name", length = 150)
    private String businessName = "SR CAR CARE";

    @Column(name = "owner_name", length = 150)
    private String ownerName = "";

    @Column(name = "phone_number", length = 30)
    private String phoneNumber = "";

    @Column(name = "whatsapp_number", length = 30)
    private String whatsappNumber = "";

    @Column(name = "email", length = 150)
    private String email = "";

    @Column(name = "address", length = 500)
    private String address = "";

    @Column(name = "working_hours", length = 255)
    private String workingHours = "Mon - Sun: 8:00 AM - 8:00 PM";

    @Column(name = "phonepe_enabled", nullable = false)
    private boolean phonepeEnabled = false;

    @Column(name = "phonepe_merchant_id", length = 100)
    private String phonepeMerchantId = "";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getWhatsappNumber() { return whatsappNumber; }
    public void setWhatsappNumber(String whatsappNumber) { this.whatsappNumber = whatsappNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }
    public boolean isPhonepeEnabled() { return phonepeEnabled; }
    public void setPhonepeEnabled(boolean phonepeEnabled) { this.phonepeEnabled = phonepeEnabled; }
    public String getPhonepeMerchantId() { return phonepeMerchantId; }
    public void setPhonepeMerchantId(String phonepeMerchantId) { this.phonepeMerchantId = phonepeMerchantId; }
}
