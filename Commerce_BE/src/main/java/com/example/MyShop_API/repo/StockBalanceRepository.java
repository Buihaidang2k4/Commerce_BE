package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.StockBalance;
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
public interface StockBalanceRepository extends JpaRepository<StockBalance, Long> {
    Optional<StockBalance> findByProductId(Long productProductId);

    // Dùng để khóa khi truy vấn vào số lượng tồn kho
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM StockBalance i WHERE i.productId =:productId")
    Optional<StockBalance> findByProductIdWithLock(Long productId);

    // Huy
    @Modifying
    @Query("""
            UPDATE StockBalance i 
            SET i.reservedQuantity =  i.reservedQuantity - :quantity 
            WHERE i.id =:inventoryId AND i.reservedQuantity >= :quantity""")
    int cancelReservation(Long inventoryId, int quantity);

    @Modifying
    @Query("""
            UPDATE StockBalance i 
            SET i.reservedQuantity = i.reservedQuantity - :qty 
            WHERE i.id = :id AND i.reservedQuantity >= :qty
            """)
    int confirmOrder(Long id, int qty);

    @Query("""
                select i
                from StockBalance i
                where (i.onHandQuantity - i.reservedQuantity) <= :threshold
            """)
    List<StockBalance> findLowStock(@Param("threshold") int threshold);


    @Query("""
                select i
                from StockBalance i
                where (i.onHandQuantity - i.reservedQuantity) =:zero
            """)
    List<StockBalance> findInventoriesByAvailableOutOfStock(@Param("zero") int zero);
    void deleteByProductId(Long productId);
}
