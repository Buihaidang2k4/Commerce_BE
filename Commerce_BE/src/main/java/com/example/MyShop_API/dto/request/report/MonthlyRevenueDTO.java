package com.example.MyShop_API.dto.request.report;

import java.math.BigDecimal;

public record MonthlyRevenueDTO(
        int month,
        BigDecimal revenue,
        int totalOrders,
        int totalProductsSold,
        BigDecimal avgOrderValue, // doanh thu trung bình mỗi đơn
        BigDecimal growthRate // tăng trưởng so với tháng trước
) {
}
