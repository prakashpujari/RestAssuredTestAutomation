package com.company.api.generator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Exports test results to Excel format
 */
public class ExcelExporter {

    public static void exportToExcel(List<TestResult> results, String filePath) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("API Test Results");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Endpoint", "Method", "Test Input", "Status Code", "Response Body", "Status", "Issue"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(createHeaderStyle(workbook));
        }

        // Create data rows
        int rowNum = 1;
        for (TestResult result : results) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(result.getEndpoint());
            row.createCell(1).setCellValue(result.getMethod());
            row.createCell(2).setCellValue(result.getTestInput() != null ? result.getTestInput() : "");
            row.createCell(3).setCellValue(result.getStatusCode());
            row.createCell(4).setCellValue(result.getResponseBody() != null ? result.getResponseBody() : "");
            row.createCell(5).setCellValue(result.getStatus());
            row.createCell(6).setCellValue(result.getIssue() != null ? result.getIssue() : "");
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to file
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
            workbook.close();
            System.out.println("Test results exported to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error exporting to Excel: " + e.getMessage());
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}