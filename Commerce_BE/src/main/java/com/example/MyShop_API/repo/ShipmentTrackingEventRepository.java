package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.ShipmentTrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentTrackingEventRepository extends JpaRepository<ShipmentTrackingEvent, Long> {
}
