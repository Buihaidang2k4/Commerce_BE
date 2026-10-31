package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxEventRepository extends JpaRepository<InboxEvent, Long> {
}
