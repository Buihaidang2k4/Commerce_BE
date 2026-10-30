package com.example.MyShop_API.service.report.revenue_report;

import com.example.MyShop_API.dto.request.report.*;

import java.util.List;

public interface IRevenueReportService {
    // Doanh thu theo tháng trong 1 năm
    List<MonthlyRevenueDTO> getMonthlyRevenue(int year);

    // Doanh thu theo quý trong 1 năm
    List<QuarterlyRevenueDTO> getQuarterlyRevenue(int year);

    // Doanh thu theo năm (nhiều năm)
    List<AnnualRevenueDTO> getAnnualRevenue(int fromYear, int toYear);

    // Doanh thu theo từng sản phẩm trong 1 tháng hoặc 1 năm
    List<ProductRevenueDTO> getRevenueByProduct(int year, int month);

    // Doanh thu theo danh mục
    List<CategoryRevenueDTO> getRevenueByCategory(int year, int month);
}
