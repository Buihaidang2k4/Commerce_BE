package com.example.MyShop_API.dto.request.report;

import java.math.BigDecimal;

public record QuarterlyRevenueDTO(
        int quarter,
        BigDecimal revenueQuarter, // doanh thu quy
        int totalOrders,
        int totalProductsSold,
        BigDecimal avgOrderValue, // doanh thu trung binh mot don
        BigDecimal growthRate
) {
}
