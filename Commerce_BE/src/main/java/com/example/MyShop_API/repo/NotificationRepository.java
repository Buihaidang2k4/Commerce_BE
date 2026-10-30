package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
