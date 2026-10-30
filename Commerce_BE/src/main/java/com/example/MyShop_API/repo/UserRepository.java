package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT e FROM User e WHERE e.id = :id")
    java.util.Optional<User> findByIdForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
}
