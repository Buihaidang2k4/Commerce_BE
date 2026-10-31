package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByProductId(Long productProductId);

    // Doanh thu theo san pham
    @Query(value = """
            SELECT
                p.id ,
                p.name ,
                SUM(oi.quantity * oi.unit_price) as revenueProduct,
                SUM(oi.quantity) as totalSold,
                COUNT(DISTINCT o.id ) as totalOrders,
                AVG(oi.unit_price) as avgPrice
            FROM order_items oi
            JOIN products p ON
                oi.product_id = p.id
            JOIN orders o ON
                oi.order_id = o.id
            JOIN payment_intents pm ON
                pm.id = o.payment_id
            WHERE
                YEAR(o.order_date) =:year
                AND MONTH(o.order_date) =:month
                AND pm.status = 'PAID'
            GROUP BY
                p.id ,
                p.name
            """, nativeQuery = true)
    List<Object[]> getRevenueByProduct(@Param("year") int year,
                                       @Param("month") int month);

    @Query(value = """
            SELECT
                c.id ,
                c.name ,
                SUM(oi.quantity * oi.unit_price ) as revenueCategory,
                SUM(oi.quantity) as totalProductSold,
                COUNT(DISTINCT o.id ) as totalOrders,
                AVG(oi.unit_price) as avgOrderValue
            FROM order_items oi
            JOIN products p ON
                oi.product_id = p.id
            JOIN categories c ON
                c.id = p.category_id
            JOIN orders o ON
                o.id = oi.order_id
            JOIN payment_intents pm ON
                o.payment_id = pm.id
            WHERE
                YEAR(o.order_date) =:year
                AND MONTH(o.order_date) =:month
                AND pm.status = 'PAID'
            GROUP BY
                c.id ,
                c.name
            """, nativeQuery = true)
    List<Object[]> getRevenueByCategory(@Param("year") int year,
                                        @Param("month") int month);


    List<OrderItem> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
}
