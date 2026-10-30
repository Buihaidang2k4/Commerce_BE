package com.example.MyShop_API.service.inventory;

import com.example.MyShop_API.dto.request.InventoryDTO;
import com.example.MyShop_API.entity.Inventory;
import com.example.MyShop_API.entity.Product;

import java.util.List;

public interface IInventoryService {
    Inventory initializeInventory(Product product, int initialStock);

    Inventory updateStock(Long productId, int newAvailable);

    boolean reserveStock(Long productId, int quantity);

    void cancelReservation(Long productId, int quantity);

    void confirmOrder(Long productId, int quantity);

    void restock(Long productId, int quantity);

    boolean checkStock(Long productId, int quantity);

    Inventory getInventoryWithLock(Long productId);

    InventoryDTO getInventoryStatus(Long productId);

    Inventory getInventoryByProductId(Long productId);

    List<InventoryDTO> getInventoryLowStock(int threshold);

    List<InventoryDTO> getInventoryOutOfStock();

}
