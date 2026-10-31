package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.ShoppingCart;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    @Query("SELECT c FROM ShoppingCart c WHERE c.userProfileId = :profileId")
    Optional<ShoppingCart> findByUserProfileId(@Param("profileId") Long profileId);
    @Query("SELECT c FROM ShoppingCart c WHERE c.id = :id")
    Optional<ShoppingCart> findByIdWithItems(@Param("id") Long id);
    @Query("SELECT c FROM ShoppingCart c WHERE c.id IN (SELECT i.cartId FROM CartItem i WHERE i.id = :id)")
    ShoppingCart findByCartItemId(@Param("id") Long itemId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ShoppingCart c WHERE c.id = :id")
    Optional<ShoppingCart> findByIdForUpdate(@Param("id") Long id);
}
