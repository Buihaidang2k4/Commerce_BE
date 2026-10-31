package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.OrderShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderShippingAddressRepository extends JpaRepository<OrderShippingAddress, Long> {

    @Query(
            """
                    SELECT a FROM OrderShippingAddress  a
                    WHERE a.orderId =:orderId
                    """

    )
    OrderShippingAddress findOrderDeliveryAddressByOrderId(@Param("orderId") Long orderId);
    void deleteByOrderId(Long orderId);
}
