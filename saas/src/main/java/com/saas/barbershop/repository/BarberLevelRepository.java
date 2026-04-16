package com.saas.barbershop.repository;

import com.saas.barbershop.entity.BarberLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BarberLevelRepository extends JpaRepository<BarberLevel, Long> {

    @Query("SELECT bl FROM BarberLevel bl WHERE bl.merchant.id = :merchantId AND bl.isDeleted = false ORDER BY bl.level ASC")
    List<BarberLevel> findByMerchantIdOrderByLevelAsc(@Param("merchantId") Long merchantId);

    @Query("SELECT bl FROM BarberLevel bl WHERE bl.merchant.id = :merchantId AND bl.isDefault = true AND bl.isDeleted = false")
    Optional<BarberLevel> findDefaultByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT bl FROM BarberLevel bl WHERE bl.id = :id AND bl.merchant.id = :merchantId AND bl.isDeleted = false")
    Optional<BarberLevel> findByIdAndMerchantId(@Param("id") Long id, @Param("merchantId") Long merchantId);
}
