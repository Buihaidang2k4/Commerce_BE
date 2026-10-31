package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    @Modifying
    @Query(value = "UPDATE UserAddress a SET a.defaultShippingAddress = false WHERE a.userProfileId = :profileId")
    void clearDefaultAddressForProfile(@Param("profileId") Long profileId);

    Optional<UserAddress> findById(Long addressId);

    List<UserAddress> findByUserProfileId(Long profileProfileId);
}
