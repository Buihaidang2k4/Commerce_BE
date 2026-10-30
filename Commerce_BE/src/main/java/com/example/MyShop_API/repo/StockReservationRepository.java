package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
}
