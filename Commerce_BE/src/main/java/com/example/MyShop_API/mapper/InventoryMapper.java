package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.InventoryDTO;
import com.example.MyShop_API.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    Inventory toInventory(InventoryDTO inventoryDTO);

    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "updatedAt", target = "updatedAt")
    InventoryDTO toInventoryDTO(Inventory inventory);

}
