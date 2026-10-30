package com.example.MyShop_API.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryDTO {
    Long inventoryId;
    Long productId;
    int available;
    int reserved;
    LocalDateTime updatedAt;
}
