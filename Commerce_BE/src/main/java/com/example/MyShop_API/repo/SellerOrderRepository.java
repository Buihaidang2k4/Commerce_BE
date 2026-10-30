package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.SellerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerOrderRepository extends JpaRepository<SellerOrder, Long> {
}
