package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.RoleCodeConstants;

import com.example.MyShop_API.utils.AuthUtils;

import com.example.MyShop_API.constants.ApiPathConstants;

import com.example.MyShop_API.dto.request.InventoryDTO;
import com.example.MyShop_API.dto.request.ReserveStockRequest;
import com.example.MyShop_API.dto.request.UpdateStockRequest;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.entity.StockBalance;
import com.example.MyShop_API.service.inventory.IStockBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPathConstants.BASE_INVENTORY)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StockBalanceController {
    IStockBalanceService inventoryService;

    @GetMapping(ApiPathConstants.INVENTORY_PRODUCT_ID)
    @Operation(summary = "Get status stock")
    ResponseEntity<ApiResponse<InventoryDTO>> getInventoryStatus(@PathVariable Long productId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", inventoryService.getInventoryStatus(productId)));
    }


    @GetMapping(ApiPathConstants.INVENTORY_OUT_OF_STOCK)
    @Operation(summary = "Get inventory out of stock")
    ResponseEntity<ApiResponse<List<InventoryDTO>>> getInventoryOutOfStock() {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", inventoryService.getInventoryOutOfStock()));
    }


    @GetMapping(ApiPathConstants.INVENTORY_LOW_STOCK_THRESHOLD)
    @Operation(summary = "Get inventory low stock")
    ResponseEntity<ApiResponse<List<InventoryDTO>>> getInventoryLowStock(@PathVariable("threshold") int threshold) {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", inventoryService.getInventoryLowStock(threshold)));
    }

    @GetMapping(ApiPathConstants.INVENTORY_CHECK_STOCK)
    @Operation(summary = "Check stock", description = "return true if enough stock ")
    ResponseEntity<ApiResponse> checkAvailability(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity) {
        boolean available = inventoryService.checkStock(productId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(200, available ? "Còn hàng" : "Hết hàng", available));
    }

    @PostMapping(ApiPathConstants.INVENTORY_UPDATE_STOCK)
    @Operation(summary = "Cập nhật tồn kho (Admin)")
    ResponseEntity<ApiResponse<InventoryDTO>> updateStock(@Valid @RequestBody UpdateStockRequest request) {
        AuthUtils.validateCheckAuth(List.of(RoleCodeConstants.ADMIN));
        StockBalance inventory = inventoryService.updateStock(request.getProductId(), request.getNewAvailability());
        InventoryDTO dto = inventoryService.getInventoryStatus(request.getProductId());
        return ResponseEntity.ok(new ApiResponse<>(200, "update stock success", dto));
    }

    @PostMapping(ApiPathConstants.INVENTORY_RESERVE)
    @Operation(summary = "[private] Order in advance")
    ResponseEntity<ApiResponse<Boolean>> reserveStock(@Valid @RequestBody ReserveStockRequest request) {
        boolean success = inventoryService.reserveStock(request.getProductId(), request.getQuantity());

        return ResponseEntity.ok(new ApiResponse<>(success ? 200 : 400,
                success ? "Success order in advance" : " Cannot enough stock in inventory", success));
    }

    @PostMapping(ApiPathConstants.INVENTORY_CANCEL_RESERVATION)
    @Operation(summary = "Cancel reservation")
    ResponseEntity<ApiResponse> cancelReservation(@Valid @RequestBody ReserveStockRequest request) {
        inventoryService.cancelReservation(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(new ApiResponse<>(200, "cancel reservation success", true));
    }

    @PostMapping(ApiPathConstants.INVENTORY_CONFIRM_ORDER)
    ResponseEntity<ApiResponse> confirmOrder(@Valid @RequestBody ReserveStockRequest request) {
        inventoryService.confirmOrder(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(new ApiResponse<>(200, "confirm order success", true));
    }

    @PostMapping(ApiPathConstants.INVENTORY_RESTOCK)
    @Operation(summary = "[private] restock (cancel order)")
    ResponseEntity<ApiResponse> cancelOrder(@Valid @RequestBody ReserveStockRequest request) {
        inventoryService.restock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(new ApiResponse<>(200, "restock success", true));
    }

}
