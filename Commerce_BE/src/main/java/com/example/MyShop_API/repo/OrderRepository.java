package com.example.MyShop_API.repo;

import com.example.MyShop_API.constants.OrderStatus;
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
    @Query("SELECT o FROM Order o WHERE o.profileId =:profileId")
    List<Order> findByProfileId(@Param("profileId") Long profileId);

    List<Order> findByOrderStatus(String orderStatus);

    int countByProfileIdAndCouponId(Long profileProfileId, Long couponCouponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.orderId = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);


    @Modifying
    @Query("UPDATE Order o SET o.orderStatus=:status WHERE o.orderId=:orderId")
    void updateStatus(@Param("orderId") Long orderId, @Param("status") String status);

    @Query("""
            SELECT COUNT(o) > 0
            FROM Order o
            JOIN OrderItem oi ON oi.orderId = o.orderId
            WHERE o.profileId = :profileId
              AND oi.productId = :productId
              AND o.orderId = :orderId
              AND o.orderStatus IN :statuses
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
            	SUM(oi.quantity * oi.price ) as revenue,
            	COUNT(DISTINCT o.order_id ) as totalOrders,
            	SUM(oi.quantity) as totalProductSoild
            FROM
            	orders o
            JOIN order_items oi ON
            	o.order_id = oi.order_id
            JOIN payments p ON
            	o.payment_id = p.payment_id
            WHERE
            	YEAR(o.order_date) =:year
            	AND p.payment_status = 'PAID'
            GROUP BY MONTH(o.order_date )
            ORDER By month
            """, nativeQuery = true)
    List<Object[]> getMonthlyRevenue(@Param("year") int year);

    // Doanh thu quy
    @Query(value = """
            SELECT
            	QUARTER(o.order_date) as quater,
            	SUM(oi.quantity * oi.price) as revenueQuater,
            	COUNT(DISTINCT o.order_id) as totalOrders,
            	SUM(oi.quantity) as totalProductSold
            FROM
            	orders o
            JOIN order_items oi ON
            	o.order_id = oi.order_id
            JOIN payments p ON
            	o.payment_id = p.payment_id
            WHERE
            	YEAR(o.order_date) =:year
            	and p.payment_status = 'PAID'
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
            	SUM(oi.quantity * oi.price) as revenueYear,
            	COUNT(DISTINCT o.order_id) as totalOrders,
            	SUM(oi.quantity) as totalProductSold
            FROM
            	orders o
            JOIN order_items oi ON
            	o.order_id = oi.order_id
            JOIN payments p ON
            	o.payment_id = p.payment_id
            WHERE
            	YEAR(o.order_date) BETWEEN :fromYear AND :toYear\s
            	and p.payment_status = 'PAID'
            GROUP BY
            	QUARTER(o.order_date)
            ORDER BY
            	year
            """, nativeQuery = true)
    List<Object[]> getAnnualRevenue(@Param("fromYear") int fromYear,
                                    @Param("toYear") int toYear);

    Page<Order> findByOrderStatus(String orderStatus, Pageable pageable);
}
