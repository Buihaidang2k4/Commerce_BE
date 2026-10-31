package com.example.MyShop_API.repo;

import com.example.MyShop_API.constants.OrderStatusConstants;
import com.example.MyShop_API.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.userProfileId =:profileId")
    List<Order> findByUserProfileId(@Param("profileId") Long profileId);

    List<Order> findByStatus(String orderStatus);

    int countByUserProfileIdAndCouponId(Long profileProfileId, Long couponCouponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);


    @Modifying
    @Query("UPDATE Order o SET o.status=:status WHERE o.id=:orderId")
    void updateStatus(@Param("orderId") Long orderId, @Param("status") String status);

    @Query("""
            SELECT COUNT(o) > 0
            FROM Order o
            JOIN OrderItem oi ON oi.orderId = o.id
            WHERE o.userProfileId = :profileId
              AND oi.productId = :productId
              AND o.id = :orderId
              AND o.status IN :statuses
            """)
    boolean hasPurchasedProductInOrder(
            @Param("profileId") Long profileId,
            @Param("productId") Long productId,
            @Param("orderId") Long orderId,
            @Param("statuses") List<String> statuses);

    // Doanh thu thang
    @Query(value = """
            SELECT
                MONTH(o.order_date) as month,
                SUM(oi.quantity * oi.unit_price ) as revenue,
                COUNT(DISTINCT o.id ) as totalOrders,
                SUM(oi.quantity) as totalProductSoild
            FROM orders o
            JOIN order_items oi ON
                o.id = oi.order_id
            JOIN payment_intents p ON
                o.payment_id = p.id
            WHERE
                YEAR(o.order_date) =:year
                AND p.status = 'PAID'
            GROUP BY MONTH(o.order_date )
            ORDER By month
            """, nativeQuery = true)
    List<Object[]> getMonthlyRevenue(@Param("year") int year);

    // Doanh thu quy
    @Query(value = """
            SELECT
                QUARTER(o.order_date) as quater,
                SUM(oi.quantity * oi.unit_price) as revenueQuater,
                COUNT(DISTINCT o.id) as totalOrders,
                SUM(oi.quantity) as totalProductSold
            FROM orders o
            JOIN order_items oi ON
                o.id = oi.order_id
            JOIN payment_intents p ON
                o.payment_id = p.id
            WHERE
                YEAR(o.order_date) =:year
                and p.status = 'PAID'
            GROUP BY
                QUARTER(o.order_date)
            ORDER BY
                quater
            """, nativeQuery = true)
    List<Object[]> getQuarterlyRevenue(@Param("year") int year);

    // Doanh thu theo nam
    @Query(value = """
                SELECT
                QUARTER(o.order_date) as year,
                SUM(oi.quantity * oi.unit_price) as revenueYear,
                COUNT(DISTINCT o.id) as totalOrders,
                SUM(oi.quantity) as totalProductSold
            FROM orders o
            JOIN order_items oi ON
                o.id = oi.order_id
            JOIN payment_intents p ON
                o.payment_id = p.id
            WHERE
                YEAR(o.order_date) BETWEEN :fromYear AND :toYear\s
                and p.status = 'PAID'
            GROUP BY
                QUARTER(o.order_date)
            ORDER BY
                year
            """, nativeQuery = true)
    List<Object[]> getAnnualRevenue(@Param("fromYear") int fromYear,
                                    @Param("toYear") int toYear);

    Page<Order> findByStatus(String orderStatus, Pageable pageable);
}
