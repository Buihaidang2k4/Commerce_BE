package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Modifying
    @Query(value = "UPDATE Address a SET a.isDefault = false WHERE a.profile.profileId = :profileId")
    void clearDefaultAddressForProfile(@Param("profileId") Long profileId);

    Optional<Address> findById(Long addressId);

    List<Address> findByProfile_ProfileId(Long profileProfileId);
}
