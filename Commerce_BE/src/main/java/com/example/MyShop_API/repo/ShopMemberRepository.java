package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.ShopMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopMemberRepository extends JpaRepository<ShopMember, Long> {
}
