package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.FlashSalePurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashSalePurchaseRepository extends JpaRepository<FlashSalePurchase, Long> {
}
