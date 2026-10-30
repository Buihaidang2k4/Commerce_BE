package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Cart;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByProfileId(Long profileId);
    @Query("SELECT c FROM Cart c WHERE c.profileId = :profileId")
    Optional<Cart> findByUserProfileId(@Param("profileId") Long profileId);
    @Query("SELECT c FROM Cart c WHERE c.cartId = :id")
    Optional<Cart> findByIdWithItems(@Param("id") Long id);
    @Query("SELECT c FROM Cart c WHERE c.cartId IN (SELECT i.cartId FROM CartItem i WHERE i.cartItemId = :id)")
    Cart findByCartItems_CartItemId(@Param("id") Long itemId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cart c WHERE c.cartId = :id")
    Optional<Cart> findByIdForUpdate(@Param("id") Long id);
}
