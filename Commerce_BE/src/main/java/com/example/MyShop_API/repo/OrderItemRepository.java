package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByProductProductId(Long productProductId);

    // Doanh thu theo san pham
    @Query(value = """
            SELECT
            	p.product_id ,
            	p.product_name ,
            	SUM(oi.quantity * oi.price) as revenueProduct,
            	SUM(oi.quantity) as totalSold,
            	COUNT(DISTINCT o.order_id ) as totalOrders,
            	AVG(oi.price) as avgPrice
            FROM
            	order_items oi
            JOIN products p ON
            	oi.product_id = p.product_id
            JOIN orders o ON
            	oi.order_id = o.order_id
            JOIN payments pm ON
            	pm.payment_id = o.payment_id
            WHERE
            	YEAR(o.order_date) =:year
            	AND MONTH(o.order_date) =:month
            	AND pm.payment_status = 'PAID'
            GROUP BY
            	p.product_id ,
            	p.product_name
            """, nativeQuery = true)
    List<Object[]> getRevenueByProduct(@Param("year") int year,
                                       @Param("month") int month);

    @Query(value = """
            SELECT
            	c.category_id ,
            	c.category_name ,
            	SUM(oi.quantity * oi.price ) as revenueCategory,
            	SUM(oi.quantity) as totalProductSold,
            	COUNT(DISTINCT o.order_id ) as totalOrders,
            	AVG(oi.price) as avgOrderValue
            FROM
            	order_items oi
            JOIN products p ON
            	oi.product_id = p.product_id
            JOIN categories c ON
            	c.category_id = p.category_id
            JOIN orders o ON
            	o.order_id = oi.order_id
            JOIN payments pm ON
            	o.payment_id = pm.payment_id
            WHERE
            	YEAR(o.order_date) =:year
            	AND MONTH(o.order_date) =:month
            	AND pm.payment_status = 'PAID'
            GROUP BY
            	c.category_id ,
            	c.category_name
            """, nativeQuery = true)
    List<Object[]> getRevenueByCategory(@Param("year") int year,
                                        @Param("month") int month);


}
