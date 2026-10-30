package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {
}
