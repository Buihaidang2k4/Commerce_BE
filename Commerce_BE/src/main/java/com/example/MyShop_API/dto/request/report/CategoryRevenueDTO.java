package com.example.MyShop_API.dto.request.report;

import java.math.BigDecimal;

public record CategoryRevenueDTO(
        Long categoryId,
        String categoryName,
        BigDecimal revenueCategory,
        int totalProductsSold,
        int totalOrders,
        BigDecimal averageOrderValue // gia tri trung binh moi don trong danh muc
) {
}
