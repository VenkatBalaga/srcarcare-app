package com.srcarcare.app.service;

import com.srcarcare.app.dto.ReportData;
import com.srcarcare.app.entity.*;
import com.srcarcare.app.repository.CarServiceBookingRepository;
import com.srcarcare.app.repository.CarWashBookingRepository;
import com.srcarcare.app.repository.RentalBookingRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
public class ReportService {

    private final RentalBookingRepository rentalBookingRepository;
    private final CarWashBookingRepository carWashBookingRepository;
    private final CarServiceBookingRepository carServiceBookingRepository;

    public ReportService(RentalBookingRepository rentalBookingRepository,
                          CarWashBookingRepository carWashBookingRepository,
                          CarServiceBookingRepository carServiceBookingRepository) {
        this.rentalBookingRepository = rentalBookingRepository;
        this.carWashBookingRepository = carWashBookingRepository;
        this.carServiceBookingRepository = carServiceBookingRepository;
    }

    public ReportData generate(String period, LocalDate referenceDate) {
        LocalDate ref = referenceDate != null ? referenceDate : LocalDate.now();
        LocalDate from;
        LocalDate to;

        switch (period.toUpperCase(Locale.ROOT)) {
            case "WEEKLY" -> {
                WeekFields wf = WeekFields.of(Locale.getDefault());
                from = ref.with(wf.dayOfWeek(), 1);
                to = from.plusDays(6);
            }
            case "YEARLY" -> {
                from = ref.withDayOfYear(1);
                to = ref.withDayOfYear(ref.lengthOfYear());
            }
            default -> { // MONTHLY
                from = ref.withDayOfMonth(1);
                to = ref.withDayOfMonth(ref.lengthOfMonth());
            }
        }

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(LocalTime.MAX);

        List<RentalBooking> rentals = rentalBookingRepository.findByCreatedAtBetween(fromDateTime, toDateTime);
        List<CarWashBooking> washes = carWashBookingRepository.findByCreatedAtBetween(fromDateTime, toDateTime);
        List<CarServiceBooking> services = carServiceBookingRepository.findByCreatedAtBetween(fromDateTime, toDateTime);

        BigDecimal rentalRevenue = rentals.stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .map(RentalBooking::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal washRevenue = washes.stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .map(b -> b.getOption() != null ? b.getOption().getPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal serviceRevenue = services.stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .map(b -> b.getOption() != null ? b.getOption().getPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ReportData data = new ReportData();
        data.setPeriod(period.toUpperCase(Locale.ROOT));
        data.setFromDate(from);
        data.setToDate(to);
        data.setRentalBookings(rentals);
        data.setWashBookings(washes);
        data.setServiceBookings(services);
        data.setTotalRentalBookings(rentals.size());
        data.setTotalWashBookings(washes.size());
        data.setTotalServiceBookings(services.size());
        data.setRentalRevenue(rentalRevenue);
        data.setWashRevenue(washRevenue);
        data.setServiceRevenue(serviceRevenue);
        data.setTotalRevenue(rentalRevenue.add(washRevenue).add(serviceRevenue));
        return data;
    }

    public byte[] exportCsv(ReportData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("SR CAR CARE - ").append(data.getPeriod()).append(" REPORT (")
          .append(data.getFromDate()).append(" to ").append(data.getToDate()).append(")\n\n");

        sb.append("RENTAL BOOKINGS\n");
        sb.append("ID,Customer,Phone,Vehicle,Rental Type,Start Date,End Date,Base Price,Discount,Total Price,Promo Code,Status,Created At\n");
        for (RentalBooking b : data.getRentalBookings()) {
            sb.append(csv(b.getId())).append(",")
              .append(csv(b.getCustomerName())).append(",")
              .append(csv(b.getCustomerPhone())).append(",")
              .append(csv(b.getVehicle() != null ? b.getVehicle().getName() : "")).append(",")
              .append(csv(b.getRentalType())).append(",")
              .append(csv(b.getStartDate())).append(",")
              .append(csv(b.getEndDate())).append(",")
              .append(csv(b.getBasePrice())).append(",")
              .append(csv(b.getDiscountAmount())).append(",")
              .append(csv(b.getTotalPrice())).append(",")
              .append(csv(b.getPromoCode())).append(",")
              .append(csv(b.getStatus())).append(",")
              .append(csv(b.getCreatedAt())).append("\n");
        }

        sb.append("\nCAR WASH BOOKINGS\n");
        sb.append("ID,Customer,Phone,Option,Price,Preferred Date,Status,Created At\n");
        for (CarWashBooking b : data.getWashBookings()) {
            sb.append(csv(b.getId())).append(",")
              .append(csv(b.getCustomerName())).append(",")
              .append(csv(b.getCustomerPhone())).append(",")
              .append(csv(b.getOption() != null ? b.getOption().getName() : "")).append(",")
              .append(csv(b.getOption() != null ? b.getOption().getPrice() : "")).append(",")
              .append(csv(b.getPreferredDate())).append(",")
              .append(csv(b.getStatus())).append(",")
              .append(csv(b.getCreatedAt())).append("\n");
        }

        sb.append("\nCAR SERVICE BOOKINGS\n");
        sb.append("ID,Customer,Phone,Option,Price,Preferred Date,Status,Created At\n");
        for (CarServiceBooking b : data.getServiceBookings()) {
            sb.append(csv(b.getId())).append(",")
              .append(csv(b.getCustomerName())).append(",")
              .append(csv(b.getCustomerPhone())).append(",")
              .append(csv(b.getOption() != null ? b.getOption().getName() : "")).append(",")
              .append(csv(b.getOption() != null ? b.getOption().getPrice() : "")).append(",")
              .append(csv(b.getPreferredDate())).append(",")
              .append(csv(b.getStatus())).append(",")
              .append(csv(b.getCreatedAt())).append("\n");
        }

        sb.append("\nSUMMARY\n");
        sb.append("Total Rental Bookings,").append(data.getTotalRentalBookings()).append("\n");
        sb.append("Total Wash Bookings,").append(data.getTotalWashBookings()).append("\n");
        sb.append("Total Service Bookings,").append(data.getTotalServiceBookings()).append("\n");
        sb.append("Rental Revenue,").append(data.getRentalRevenue()).append("\n");
        sb.append("Wash Revenue,").append(data.getWashRevenue()).append("\n");
        sb.append("Service Revenue,").append(data.getServiceRevenue()).append("\n");
        sb.append("Total Revenue,").append(data.getTotalRevenue()).append("\n");

        return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private String csv(Object value) {
        if (value == null) return "";
        String s = value.toString().replace("\"", "\"\"");
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s + "\"";
        }
        return s;
    }

    public byte[] exportExcel(ReportData data) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            writeRentalSheet(workbook, headerStyle, data);
            writeWashSheet(workbook, headerStyle, data);
            writeServiceSheet(workbook, headerStyle, data);
            writeSummarySheet(workbook, headerStyle, data);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report: " + e.getMessage(), e);
        }
    }

    private void writeRentalSheet(Workbook workbook, CellStyle headerStyle, ReportData data) {
        Sheet sheet = workbook.createSheet("Rental Bookings");
        String[] headers = {"ID", "Customer", "Phone", "Vehicle", "Rental Type", "Start Date", "End Date",
                "Base Price", "Discount", "Total Price", "Promo Code", "Status", "Created At"};
        writeHeaderRow(sheet, headerStyle, headers);
        int rowIdx = 1;
        for (RentalBooking b : data.getRentalBookings()) {
            Row row = sheet.createRow(rowIdx++);
            int c = 0;
            row.createCell(c++).setCellValue(b.getId());
            row.createCell(c++).setCellValue(b.getCustomerName());
            row.createCell(c++).setCellValue(b.getCustomerPhone());
            row.createCell(c++).setCellValue(b.getVehicle() != null ? b.getVehicle().getName() : "");
            row.createCell(c++).setCellValue(b.getRentalType().toString());
            row.createCell(c++).setCellValue(b.getStartDate().toString());
            row.createCell(c++).setCellValue(b.getEndDate().toString());
            row.createCell(c++).setCellValue(b.getBasePrice().doubleValue());
            row.createCell(c++).setCellValue(b.getDiscountAmount().doubleValue());
            row.createCell(c++).setCellValue(b.getTotalPrice().doubleValue());
            row.createCell(c++).setCellValue(b.getPromoCode() != null ? b.getPromoCode() : "");
            row.createCell(c++).setCellValue(b.getStatus().toString());
            row.createCell(c).setCellValue(b.getCreatedAt().toString());
        }
        autoSize(sheet, headers.length);
    }

    private void writeWashSheet(Workbook workbook, CellStyle headerStyle, ReportData data) {
        Sheet sheet = workbook.createSheet("Car Wash Bookings");
        String[] headers = {"ID", "Customer", "Phone", "Option", "Price", "Preferred Date", "Status", "Created At"};
        writeHeaderRow(sheet, headerStyle, headers);
        int rowIdx = 1;
        for (CarWashBooking b : data.getWashBookings()) {
            Row row = sheet.createRow(rowIdx++);
            int c = 0;
            row.createCell(c++).setCellValue(b.getId());
            row.createCell(c++).setCellValue(b.getCustomerName());
            row.createCell(c++).setCellValue(b.getCustomerPhone());
            row.createCell(c++).setCellValue(b.getOption() != null ? b.getOption().getName() : "");
            row.createCell(c++).setCellValue(b.getOption() != null ? b.getOption().getPrice().doubleValue() : 0);
            row.createCell(c++).setCellValue(b.getPreferredDate().toString());
            row.createCell(c++).setCellValue(b.getStatus().toString());
            row.createCell(c).setCellValue(b.getCreatedAt().toString());
        }
        autoSize(sheet, headers.length);
    }

    private void writeServiceSheet(Workbook workbook, CellStyle headerStyle, ReportData data) {
        Sheet sheet = workbook.createSheet("Car Service Bookings");
        String[] headers = {"ID", "Customer", "Phone", "Option", "Price", "Preferred Date", "Status", "Created At"};
        writeHeaderRow(sheet, headerStyle, headers);
        int rowIdx = 1;
        for (CarServiceBooking b : data.getServiceBookings()) {
            Row row = sheet.createRow(rowIdx++);
            int c = 0;
            row.createCell(c++).setCellValue(b.getId());
            row.createCell(c++).setCellValue(b.getCustomerName());
            row.createCell(c++).setCellValue(b.getCustomerPhone());
            row.createCell(c++).setCellValue(b.getOption() != null ? b.getOption().getName() : "");
            row.createCell(c++).setCellValue(b.getOption() != null ? b.getOption().getPrice().doubleValue() : 0);
            row.createCell(c++).setCellValue(b.getPreferredDate().toString());
            row.createCell(c++).setCellValue(b.getStatus().toString());
            row.createCell(c).setCellValue(b.getCreatedAt().toString());
        }
        autoSize(sheet, headers.length);
    }

    private void writeSummarySheet(Workbook workbook, CellStyle headerStyle, ReportData data) {
        Sheet sheet = workbook.createSheet("Summary");
        Row title = sheet.createRow(0);
        title.createCell(0).setCellValue("SR CAR CARE - " + data.getPeriod() + " REPORT (" + data.getFromDate() + " to " + data.getToDate() + ")");

        String[][] rows = {
                {"Total Rental Bookings", String.valueOf(data.getTotalRentalBookings())},
                {"Total Wash Bookings", String.valueOf(data.getTotalWashBookings())},
                {"Total Service Bookings", String.valueOf(data.getTotalServiceBookings())},
                {"Rental Revenue", data.getRentalRevenue().toString()},
                {"Wash Revenue", data.getWashRevenue().toString()},
                {"Service Revenue", data.getServiceRevenue().toString()},
                {"Total Revenue", data.getTotalRevenue().toString()}
        };
        int rowIdx = 2;
        for (String[] r : rows) {
            Row row = sheet.createRow(rowIdx++);
            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(r[0]);
            labelCell.setCellStyle(headerStyle);
            row.createCell(1).setCellValue(r[1]);
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void writeHeaderRow(Sheet sheet, CellStyle headerStyle, String[] headers) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void autoSize(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
