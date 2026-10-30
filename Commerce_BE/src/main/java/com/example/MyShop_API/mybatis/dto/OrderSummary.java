package com.example.MyShop_API.mybatis.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderSummary {
    private String orderStatus;
    private long totalOrders;
    private BigDecimal totalRevenue;
}
