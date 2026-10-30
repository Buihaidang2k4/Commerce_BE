package com.example.MyShop_API.dto.request.report;

import java.math.BigDecimal;

public record AnnualRevenueDTO(
        int year,
        BigDecimal revenueYear, // tong doanh thu nam
        int totalOrders,
        int totalProductsSold,
        BigDecimal avgOrderValue, // doanh thu trung binh mot don
        BigDecimal growthRate // tang truong so voi nam truoc
) {
}
