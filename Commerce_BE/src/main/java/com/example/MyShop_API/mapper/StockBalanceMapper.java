package com.example.MyShop_API.mapper;

import com.example.MyShop_API.dto.request.InventoryDTO;
import com.example.MyShop_API.entity.StockBalance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockBalanceMapper {


    @Mapping(source = "inventoryId", target = "id")
    @Mapping(target = "onHandQuantity", expression = "java(inventoryDTO.getAvailable() + inventoryDTO.getReserved())")
    @Mapping(source = "reserved", target = "reservedQuantity")
    StockBalance toInventory(InventoryDTO inventoryDTO);


    @Mapping(source = "id", target = "inventoryId")
    @Mapping(source = "availableQuantity", target = "available")
    @Mapping(source = "reservedQuantity", target = "reserved")
    InventoryDTO toInventoryDTO(StockBalance inventory);

}
