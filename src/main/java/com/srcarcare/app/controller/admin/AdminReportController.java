package com.srcarcare.app.controller.admin;

import com.srcarcare.app.dto.ReportData;
import com.srcarcare.app.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String reports(@RequestParam(defaultValue = "MONTHLY") String period,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate referenceDate,
                           Model model) {
        ReportData data = reportService.generate(period, referenceDate);
        model.addAttribute("active", "reports");
        model.addAttribute("report", data);
        model.addAttribute("period", period.toUpperCase());
        model.addAttribute("referenceDate", referenceDate != null ? referenceDate : LocalDate.now());
        return "admin/reports";
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(@RequestParam(defaultValue = "MONTHLY") String period,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate referenceDate) {
        ReportData data = reportService.generate(period, referenceDate);
        byte[] csv = reportService.exportCsv(data);
        String filename = "srcarcare-report-" + period.toLowerCase() + "-" + data.getFromDate() + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(@RequestParam(defaultValue = "MONTHLY") String period,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate referenceDate) {
        ReportData data = reportService.generate(period, referenceDate);
        byte[] excel = reportService.exportExcel(data);
        String filename = "srcarcare-report-" + period.toLowerCase() + "-" + data.getFromDate() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}
