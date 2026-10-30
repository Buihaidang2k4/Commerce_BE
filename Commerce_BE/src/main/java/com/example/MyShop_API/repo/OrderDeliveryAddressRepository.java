package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.OrderDeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderDeliveryAddressRepository extends JpaRepository<OrderDeliveryAddress, Long> {

    @Query(
            """
                    SELECT a FROM OrderDeliveryAddress  a
                    WHERE a.order.orderId =:orderId
                    """

    )
    OrderDeliveryAddress findOrderDeliveryAddressByOrderId(@Param("orderId") Long orderId);
}
