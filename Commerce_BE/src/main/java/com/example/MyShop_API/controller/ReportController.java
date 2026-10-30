package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPath;

import com.example.MyShop_API.dto.request.report.*;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.service.report.ReportExcelService;
import com.example.MyShop_API.service.report.ReportPdfService;
import com.example.MyShop_API.service.report.revenue_report.IRevenueReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_REPORT)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {
    ReportPdfService reportPdfService;
    ReportExcelService reportExcelService;
    OrderRepository orderRepository;
    IRevenueReportService reportService;

    @NonFinal
    @Value("${report.temp-dir}")
    private String tempDir;

    @PostMapping(ApiPath.REPORT_REPORT_ORDER_PDF_ORDER_ID)
    public ResponseEntity<byte[]> generatePdfOrder(@PathVariable Long orderId) throws IOException {
        String html = reportPdfService.getReportHtml(orderId);
        byte[] pdfBytes = reportPdfService.convertHtmlToPdf(html);

        // Tạo tên file động theo thời gian hoặc ID
        String fileName = "report-" + System.currentTimeMillis() + ".pdf";
        Path filePath = Paths.get(tempDir, fileName);
        // Lưu xuống thư mục tempDir
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, pdfBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping(ApiPath.REPORT_EXPORT_ORDERS)
    public ResponseEntity<ApiResponse> exportOrders() throws IOException {
        List<Order> orders = orderRepository.findAll();
        reportExcelService.exportOrders(orders);

        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                "Xuất file Excel thành công",
                null
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(ApiPath.REPORT_MONTHLY_REVENUE)
    public List<MonthlyRevenueDTO> getMonthlyRevenue(@RequestParam int year) {
        return reportService.getMonthlyRevenue(year);
    }

    @GetMapping(ApiPath.REPORT_QUARTERLY_REVENUE)
    public List<QuarterlyRevenueDTO> getQuarterlyRevenue(@RequestParam int year) {
        return reportService.getQuarterlyRevenue(year);
    }

    @GetMapping(ApiPath.REPORT_ANNUAL_REVENUE)
    public List<AnnualRevenueDTO> getAnnualRevenue(
            @RequestParam int fromYear,
            @RequestParam int toYear) {
        return reportService.getAnnualRevenue(fromYear, toYear);
    }

    @GetMapping(ApiPath.REPORT_PRODUCT_REVENUE)
    public List<ProductRevenueDTO> getRevenueByProduct(
            @RequestParam int year,
            @RequestParam int month) {
        return reportService.getRevenueByProduct(year, month);
    }

    @GetMapping(ApiPath.REPORT_CATEGORY_REVENUE)
    public List<CategoryRevenueDTO> getRevenueByCategory(
            @RequestParam int year,
            @RequestParam int month) {
        return reportService.getRevenueByCategory(year, month);
    }
}
