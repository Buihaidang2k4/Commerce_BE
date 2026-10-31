package com.example.MyShop_API.service.inventory;

import com.example.MyShop_API.dto.request.InventoryDTO;
import com.example.MyShop_API.entity.StockBalance;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.mapper.StockBalanceMapper;
import com.example.MyShop_API.repo.StockBalanceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockBalanceService implements IStockBalanceService {
    com.example.MyShop_API.repo.ProductRepository productRepository;
    StockBalanceRepository inventoryRepository;
    StockBalanceMapper inventoryMapper;

    // 1. Khởi tạo tồn kho khi tạo sản phẩm
    @Override
    @Transactional
    public StockBalance initializeInventory(Product product, int initialStock) {
        productRepository.findByIdForUpdate(product.getId()).orElseThrow(() -> new AppException(CommonMessageConstants.PRODUCT_NOT_EXISTED));
        var existing = inventoryRepository.findByProductId(product.getId());
        if (existing.isPresent()) return existing.get();
        if (initialStock < 0) throw new AppException(CommonMessageConstants.VALIDATION_ERROR);
        StockBalance inventory = StockBalance.builder().productId(product.getId()).onHandQuantity(initialStock).reservedQuantity(0).build();
        return inventoryRepository.save(inventory);
    }

    // 2. Cập nhật tồn kho (admin)
    @Override
    @Transactional
    public StockBalance updateStock(Long productId, int newAvailable) {
        if (newAvailable < 0) throw new AppException(CommonMessageConstants.VALIDATION_ERROR);
        StockBalance inventory = getInventoryWithLock(productId);
        inventory.setUpdatedAt(Instant.now());
        inventory.setOnHandQuantity(newAvailable + inventory.getReservedQuantity());
        return inventoryRepository.save(inventory);
    }

    // 3. Đặt trước (khi thêm vào giỏ)
    @Transactional
    @Override
    public boolean reserveStock(Long productId, int quantity) {
        if (quantity <= 0) throw new AppException(CommonMessageConstants.VALIDATION_ERROR);
        StockBalance inventory = getInventoryWithLock(productId);

        if (!inventory.canReserve(quantity)) {
            log.warn("Not enough stock for product {} : needed {} : available {}", productId, quantity, inventory.getAvailableQuantity());
            return false;
        }

        // Nếu có đủ thì cho đặt trước
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventory.setUpdatedAt(Instant.now());
        inventoryRepository.save(inventory);
        return true;
    }

    // 4. Hủy đặt trước (xóa khỏi giỏ)
    @Override
    @Transactional
    public void cancelReservation(Long productId, int quantity) {
        if (quantity <= 0) throw new AppException(CommonMessageConstants.VALIDATION_ERROR);
        StockBalance inventory = getInventoryWithLock(productId);
        if (inventory.getReservedQuantity() < quantity) {
            throw new AppException(CommonMessageConstants.NOT_ENOUGH_RESERVED_STOCK);
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    // 5. Xác nhận đơn hàng (thanh toán thành công)
    @Override
    @Transactional
    public void confirmOrder(Long productId, int quantity) {
        if (quantity <= 0) throw new AppException(CommonMessageConstants.VALIDATION_ERROR);
        StockBalance inventory = getInventoryWithLock(productId);
        if (inventory.getReservedQuantity() < quantity) {
            throw new AppException(CommonMessageConstants.NOT_ENOUGH_RESERVED_STOCK);
        }

        inventory.setOnHandQuantity(inventory.getOnHandQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    // 6. Hủy đơn hàng (hoàn hàng)
    @Override
    @Transactional
    public void restock(Long productId, int quantity) {
        if (quantity <= 0) throw new AppException(CommonMessageConstants.VALIDATION_ERROR);
        StockBalance inventory = getInventoryWithLock(productId);

        if (inventory.getReservedQuantity() < quantity)
            throw new AppException(CommonMessageConstants.NOT_ENOUGH_RESERVED_STOCK);

        // Tăng số lượng hàng
        // Giảm số lượng đặt trước
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    // 7. Kiểm tra tồn kho trước thanh toán
    @Override
    public boolean checkStock(Long productId, int quantity) {
        return inventoryRepository.findByProductId(productId).map(inv -> inv.canReserve(quantity)).orElse(false);
    }

    @Override
    @Transactional
    public StockBalance getInventoryWithLock(Long productId) {
        return inventoryRepository.findByProductIdWithLock(productId).orElseThrow(() -> new AppException(CommonMessageConstants.INVENTORY_DOES_NOT_EXIST));
    }

    // 8. Lấy thông tin tồn kho
    @Override
    @Transactional(readOnly = true)
    public InventoryDTO getInventoryStatus(Long productId) {
        StockBalance inventory = getInventoryByProductId(productId);
        return inventoryMapper.toInventoryDTO(inventory);
    }


    @Override
    @Transactional(readOnly = true)
    public StockBalance getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId).orElseThrow(() -> new AppException(CommonMessageConstants.INVENTORY_DOES_NOT_EXIST, productId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getInventoryLowStock(int threshold) {
        List<StockBalance> inventories = inventoryRepository.findLowStock(threshold);
        return inventories.stream().map(inventoryMapper::toInventoryDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getInventoryOutOfStock() {
        List<StockBalance> inventories = inventoryRepository.findInventoriesByAvailableOutOfStock(0);
        return inventories.stream().map(inventoryMapper::toInventoryDTO).toList();
    }


}
