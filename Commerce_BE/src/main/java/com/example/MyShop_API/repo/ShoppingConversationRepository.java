package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.ShoppingConversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingConversationRepository extends JpaRepository<ShoppingConversation, Long> {
}
