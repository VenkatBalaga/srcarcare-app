package com.srcarcare.app.dto;

import com.srcarcare.app.entity.CarServiceBooking;
import com.srcarcare.app.entity.CarWashBooking;
import com.srcarcare.app.entity.RentalBooking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReportData {

    private String period;
    private LocalDate fromDate;
    private LocalDate toDate;

    private List<RentalBooking> rentalBookings;
    private List<CarWashBooking> washBookings;
    private List<CarServiceBooking> serviceBookings;

    private int totalRentalBookings;
    private int totalWashBookings;
    private int totalServiceBookings;

    private BigDecimal rentalRevenue = BigDecimal.ZERO;
    private BigDecimal washRevenue = BigDecimal.ZERO;
    private BigDecimal serviceRevenue = BigDecimal.ZERO;
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }
    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }
    public List<RentalBooking> getRentalBookings() { return rentalBookings; }
    public void setRentalBookings(List<RentalBooking> rentalBookings) { this.rentalBookings = rentalBookings; }
    public List<CarWashBooking> getWashBookings() { return washBookings; }
    public void setWashBookings(List<CarWashBooking> washBookings) { this.washBookings = washBookings; }
    public List<CarServiceBooking> getServiceBookings() { return serviceBookings; }
    public void setServiceBookings(List<CarServiceBooking> serviceBookings) { this.serviceBookings = serviceBookings; }
    public int getTotalRentalBookings() { return totalRentalBookings; }
    public void setTotalRentalBookings(int totalRentalBookings) { this.totalRentalBookings = totalRentalBookings; }
    public int getTotalWashBookings() { return totalWashBookings; }
    public void setTotalWashBookings(int totalWashBookings) { this.totalWashBookings = totalWashBookings; }
    public int getTotalServiceBookings() { return totalServiceBookings; }
    public void setTotalServiceBookings(int totalServiceBookings) { this.totalServiceBookings = totalServiceBookings; }
    public BigDecimal getRentalRevenue() { return rentalRevenue; }
    public void setRentalRevenue(BigDecimal rentalRevenue) { this.rentalRevenue = rentalRevenue; }
    public BigDecimal getWashRevenue() { return washRevenue; }
    public void setWashRevenue(BigDecimal washRevenue) { this.washRevenue = washRevenue; }
    public BigDecimal getServiceRevenue() { return serviceRevenue; }
    public void setServiceRevenue(BigDecimal serviceRevenue) { this.serviceRevenue = serviceRevenue; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
}
