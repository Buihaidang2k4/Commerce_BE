package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.ShopMembership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopMembershipRepository extends JpaRepository<ShopMembership, Long> {
}
