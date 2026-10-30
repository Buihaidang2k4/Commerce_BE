package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {
}
