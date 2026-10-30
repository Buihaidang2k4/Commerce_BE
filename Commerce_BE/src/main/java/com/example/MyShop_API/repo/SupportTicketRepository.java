package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
}
