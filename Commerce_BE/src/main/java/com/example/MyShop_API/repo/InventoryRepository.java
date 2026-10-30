package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProduct_ProductId(Long productProductId);

    // Dùng để khóa khi truy vấn vào số lượng tồn kho
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.product.productId =:productId")
    Optional<Inventory> findByProductIdWithLock(Long productId);

    // Huy
    @Modifying
    @Query("""
            UPDATE Inventory i 
            SET i.reserved =  i.reserved - :quantity 
            WHERE i.inventoryId =:inventoryId AND i.reserved >= :quantity""")
    int cancelReservation(Long inventoryId, int quantity);

    @Modifying
    @Query("""
            UPDATE Inventory i 
            SET i.reserved = i.reserved - :qty 
            WHERE i.inventoryId = :id AND i.reserved >= :qty
            """)
    int confirmOrder(Long id, int qty);

    @Query("""
                select i
                from Inventory i
                where i.available <= :threshold
            """)
    List<Inventory> findLowStock(@Param("threshold") int threshold);


    @Query("""
                select i
                from Inventory i
                where i.available =:zero
            """)
    List<Inventory> findInventoriesByAvailableOutOfStock(@Param("zero") int zero);
}
