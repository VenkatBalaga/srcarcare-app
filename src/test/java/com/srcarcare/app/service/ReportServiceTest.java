package com.srcarcare.app.service;

import com.srcarcare.app.dto.ReportData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Test
    void generatesWeeklyMonthlyYearlyReports() {
        for (String period : new String[] {"WEEKLY", "MONTHLY", "YEARLY"}) {
            ReportData data = reportService.generate(period, LocalDate.now());
            assertEquals(period, data.getPeriod());
            assertNotNull(data.getFromDate());
            assertNotNull(data.getToDate());
            assertTrue(!data.getToDate().isBefore(data.getFromDate()));
        }
    }

    @Test
    void exportsNonEmptyCsvAndExcel() {
        ReportData data = reportService.generate("MONTHLY", LocalDate.now());
        byte[] csv = reportService.exportCsv(data);
        byte[] excel = reportService.exportExcel(data);

        assertTrue(csv.length > 0);
        assertTrue(excel.length > 0);
        // XLSX files are ZIP archives and start with the "PK" magic bytes.
        assertEquals(0x50, excel[0] & 0xFF);
        assertEquals(0x4B, excel[1] & 0xFF);
    }
}
