package com.example.MyShop_API.service.inventory;

import com.example.MyShop_API.dto.request.InventoryDTO;
import com.example.MyShop_API.entity.Inventory;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.InventoryMapper;
import com.example.MyShop_API.repo.InventoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryService implements IInventoryService {
    com.example.MyShop_API.repo.ProductRepository productRepository;
    InventoryRepository inventoryRepository;
    InventoryMapper inventoryMapper;

    // 1. Khởi tạo tồn kho khi tạo sản phẩm
    @Override
    @Transactional
    public Inventory initializeInventory(Product product, int initialStock) {
        productRepository.findByIdForUpdate(product.getProductId()).orElseThrow(() -> new AppException(CommonMessageUtils.PRODUCT_NOT_EXISTED));
        var existing = inventoryRepository.findByProductId(product.getProductId());
        if (existing.isPresent()) return existing.get();
        if (initialStock < 0) throw new AppException(CommonMessageUtils.VALIDATION_ERROR);
        Inventory inventory = Inventory.builder().productId(product.getProductId()).available(initialStock).reserved(0).build();
        return inventoryRepository.save(inventory);
    }

    // 2. Cập nhật tồn kho (admin)
    @Override
    @Transactional
    public Inventory updateStock(Long productId, int newAvailable) {
        if (newAvailable < 0) throw new AppException(CommonMessageUtils.VALIDATION_ERROR);
        Inventory inventory = getInventoryWithLock(productId);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setAvailable(newAvailable);
        return inventoryRepository.save(inventory);
    }

    // 3. Đặt trước (khi thêm vào giỏ)
    @Transactional
    @Override
    public boolean reserveStock(Long productId, int quantity) {
        if (quantity <= 0) throw new AppException(CommonMessageUtils.VALIDATION_ERROR);
        Inventory inventory = getInventoryWithLock(productId);

        if (!inventory.canReserve(quantity)) {
            log.warn("Not enough stock for product {} : needed {} : available {}", productId, quantity, inventory.getAvailable());
            return false;
        }

        // Nếu có đủ thì cho đặt trước
        inventory.setAvailable(inventory.getAvailable() - quantity);
        inventory.setReserved(inventory.getReserved() + quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);
        return true;
    }

    // 4. Hủy đặt trước (xóa khỏi giỏ)
    @Override
    @Transactional
    public void cancelReservation(Long productId, int quantity) {
        if (quantity <= 0) throw new AppException(CommonMessageUtils.VALIDATION_ERROR);
        Inventory inventory = getInventoryWithLock(productId);
        if (inventory.getReserved() < quantity) {
            throw new AppException(CommonMessageUtils.NOT_ENOUGH_RESERVED_STOCK);
        }

        inventory.setReserved(inventory.getReserved() - quantity);
        inventory.setAvailable(inventory.getAvailable() + quantity);
        inventoryRepository.save(inventory);
    }

    // 5. Xác nhận đơn hàng (thanh toán thành công)
    @Override
    @Transactional
    public void confirmOrder(Long productId, int quantity) {
        if (quantity <= 0) throw new AppException(CommonMessageUtils.VALIDATION_ERROR);
        Inventory inventory = getInventoryWithLock(productId);
        if (inventory.getReserved() < quantity) {
            throw new AppException(CommonMessageUtils.NOT_ENOUGH_RESERVED_STOCK);
        }

        inventory.setReserved(inventory.getReserved() - quantity);
        inventoryRepository.save(inventory);
    }

    // 6. Hủy đơn hàng (hoàn hàng)
    @Override
    @Transactional
    public void restock(Long productId, int quantity) {
        if (quantity <= 0) throw new AppException(CommonMessageUtils.VALIDATION_ERROR);
        Inventory inventory = getInventoryWithLock(productId);

        if (inventory.getReserved() < quantity)
            throw new AppException(CommonMessageUtils.NOT_ENOUGH_RESERVED_STOCK);

        // Tăng số lượng hàng
        inventory.setAvailable(inventory.getAvailable() + quantity);
        // Giảm số lượng đặt trước
        inventory.setReserved(inventory.getReserved() - quantity);
        inventoryRepository.save(inventory);
    }

    // 7. Kiểm tra tồn kho trước thanh toán
    @Override
    public boolean checkStock(Long productId, int quantity) {
        return inventoryRepository.findByProductId(productId).map(inv -> inv.canReserve(quantity)).orElse(false);
    }

    @Override
    @Transactional
    public Inventory getInventoryWithLock(Long productId) {
        return inventoryRepository.findByProductIdWithLock(productId).orElseThrow(() -> new AppException(CommonMessageUtils.INVENTORY_DOES_NOT_EXIST));
    }

    // 8. Lấy thông tin tồn kho
    @Override
    @Transactional(readOnly = true)
    public InventoryDTO getInventoryStatus(Long productId) {
        Inventory inventory = getInventoryByProductId(productId);
        return inventoryMapper.toInventoryDTO(inventory);
    }


    @Override
    @Transactional(readOnly = true)
    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId).orElseThrow(() -> new AppException(CommonMessageUtils.INVENTORY_DOES_NOT_EXIST, productId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getInventoryLowStock(int threshold) {
        List<Inventory> inventories = inventoryRepository.findLowStock(threshold);
        return inventories.stream().map(inventoryMapper::toInventoryDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getInventoryOutOfStock() {
        List<Inventory> inventories = inventoryRepository.findInventoriesByAvailableOutOfStock(0);
        return inventories.stream().map(inventoryMapper::toInventoryDTO).toList();
    }


}
