package com.example.MyShop_API.mybatis.mapper;

import com.example.MyShop_API.mybatis.dto.OrderSummary;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

/** Read-only example for complex reports; uses the existing JPA datasource. */
public interface OrderReportMapper {
    List<OrderSummary> summarizeByStatus(@Param("fromDate") LocalDate fromDate,
                                        @Param("toDate") LocalDate toDate);
}
