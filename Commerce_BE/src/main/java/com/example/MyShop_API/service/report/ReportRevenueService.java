package com.example.MyShop_API.service.report;

import com.example.MyShop_API.dto.request.report.*;
import com.example.MyShop_API.repo.OrderItemRepository;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.service.report.revenue_report.IRevenueReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportRevenueService implements IRevenueReportService {
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyRevenueDTO> getMonthlyRevenue(int year) {
        List<MonthlyRevenueDTO> raw = orderRepository.getMonthlyRevenue(year).stream()
                .map(r -> new MonthlyRevenueDTO(
                        ((Number) r[0]).intValue(),
                        (BigDecimal) r[1],
                        ((Number) r[2]).intValue(),
                        ((Number) r[3]).intValue(),
                        calculateAverage((BigDecimal) r[1], ((Number) r[2]).intValue()),
                        BigDecimal.ZERO // growthRate tính sau
                ))
                .toList();

        return calculateGrowth(
                raw,
                MonthlyRevenueDTO::revenue,
                (dto, growth) -> new MonthlyRevenueDTO(
                        dto.month(),
                        dto.revenue(),
                        dto.totalOrders(),
                        dto.totalProductsSold(),
                        dto.avgOrderValue(),
                        growth
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuarterlyRevenueDTO> getQuarterlyRevenue(int year) {
        List<QuarterlyRevenueDTO> raw = orderRepository.getQuarterlyRevenue(year).stream()
                .map(r -> new QuarterlyRevenueDTO(
                        ((Number) r[0]).intValue(),
                        (BigDecimal) r[1],
                        ((Number) r[2]).intValue(),
                        ((Number) r[3]).intValue(),
                        calculateAverage((BigDecimal) r[1], ((Number) r[2]).intValue()),
                        BigDecimal.ZERO
                ))
                .toList();

        return calculateGrowth(
                raw,
                QuarterlyRevenueDTO::revenueQuarter,
                (dto, growth) -> new QuarterlyRevenueDTO(
                        dto.quarter(),
                        dto.revenueQuarter(),
                        dto.totalOrders(),
                        dto.totalProductsSold(),
                        dto.avgOrderValue(),
                        growth
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnualRevenueDTO> getAnnualRevenue(int fromYear, int toYear) {
        List<AnnualRevenueDTO> raw = orderRepository.getAnnualRevenue(fromYear, toYear).stream()
                .map(r -> new AnnualRevenueDTO(
                        ((Number) r[0]).intValue(),
                        (BigDecimal) r[1],
                        ((Number) r[2]).intValue(),
                        ((Number) r[3]).intValue(),
                        calculateAverage((BigDecimal) r[1], ((Number) r[2]).intValue()),
                        BigDecimal.ZERO
                ))
                .toList();

        return calculateGrowth(
                raw,
                AnnualRevenueDTO::revenueYear,
                (dto, growth) -> new AnnualRevenueDTO(
                        dto.year(),
                        dto.revenueYear(),
                        dto.totalOrders(),
                        dto.totalProductsSold(),
                        dto.avgOrderValue(),
                        growth
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductRevenueDTO> getRevenueByProduct(int year, int month) {
        return orderItemRepository.getRevenueByProduct(year, month).stream()
                .map(r -> new ProductRevenueDTO(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (BigDecimal) r[2],
                        ((Number) r[3]).intValue(),
                        ((Number) r[4]).intValue(),
                        (BigDecimal) r[5]
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryRevenueDTO> getRevenueByCategory(int year, int month) {
        return orderItemRepository.getRevenueByCategory(year, month).stream()
                .map(r -> new CategoryRevenueDTO(
                        ((Number) r[0]).longValue(),
                        (String) r[1],
                        (BigDecimal) r[2],
                        ((Number) r[3]).intValue(),
                        ((Number) r[4]).intValue(),
                        (BigDecimal) r[5]
                ))
                .toList();
    }


    private <T> List<T> calculateGrowth(List<T> list, Function<T, BigDecimal> revenueGetter, BiFunction<T, BigDecimal, T> dtoWithGrowthCreator
    ) {
        BigDecimal previous = BigDecimal.ZERO;
        List<T> result = new ArrayList<>();

        for (T dto : list) {
            BigDecimal currentRevenue = revenueGetter.apply(dto);
            BigDecimal growth;

            if (previous.compareTo(BigDecimal.ZERO) == 0) {
                growth = BigDecimal.ZERO;
            } else {
                growth = currentRevenue
                        .subtract(previous)
                        .divide(previous, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            result.add(dtoWithGrowthCreator.apply(dto, growth));
            previous = currentRevenue;
        }

        return result;
    }


    private BigDecimal calculateAverage(BigDecimal revenue, int totalOrders) {
        if (totalOrders == 0) {
            return BigDecimal.ZERO;
        }

        return revenue.divide(
                BigDecimal.valueOf(totalOrders),
                2,                    // scale: 2 chữ số sau dấu phẩy
                RoundingMode.HALF_UP // làm tròn chuẩn
        );
    }
}
