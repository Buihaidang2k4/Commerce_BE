package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.CheckoutQuote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutQuoteRepository extends JpaRepository<CheckoutQuote, Long> {
}
