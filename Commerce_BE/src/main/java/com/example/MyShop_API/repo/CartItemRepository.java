package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.CartItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    Optional<CartItem> findByIdAndCartId(Long cartItemId, Long cartId);
    long deleteByProductId(Long productId);
    @Modifying @Query("DELETE FROM CartItem c WHERE c.id = :id")
    void deleteByIdDirect(@Param("id") Long id);
    @Modifying @Query("DELETE FROM CartItem c WHERE c.cartId = :cartId")
    int hardDeleteByCartId(@Param("cartId") Long cartId);
    @Query("SELECT ci FROM CartItem ci WHERE ci.id IN :ids AND ci.cartId IN (SELECT c.id FROM ShoppingCart c WHERE c.userProfileId = :profileId)")
    List<CartItem> findALlByIdsAndProfile(@Param("ids") List<Long> ids, @Param("profileId") Long profileId);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("SELECT ci FROM CartItem ci WHERE ci.id IN :ids")
    List<CartItem> lockCartItems(@Param("ids") List<Long> ids);
}
