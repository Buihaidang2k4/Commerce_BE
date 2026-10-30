package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}
