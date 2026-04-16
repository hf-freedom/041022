package com.saas.barbershop.repository;

import com.saas.barbershop.entity.BarberHairstyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BarberHairstyleRepository extends JpaRepository<BarberHairstyle, Long> {

    @Query("SELECT bh FROM BarberHairstyle bh WHERE bh.barber.id = :barberId AND bh.isDeleted = false")
    List<BarberHairstyle> findByBarberId(@Param("barberId") Long barberId);

    @Query("SELECT bh FROM BarberHairstyle bh WHERE bh.barber.id = :barberId AND bh.hairstyleType.id = :hairstyleTypeId AND bh.isDeleted = false")
    Optional<BarberHairstyle> findByBarberIdAndHairstyleTypeId(@Param("barberId") Long barberId, @Param("hairstyleTypeId") Long hairstyleTypeId);

    @Query("SELECT bh FROM BarberHairstyle bh WHERE bh.hairstyleType.id = :hairstyleTypeId AND bh.isDeleted = false")
    List<BarberHairstyle> findByHairstyleTypeId(@Param("hairstyleTypeId") Long hairstyleTypeId);

    @Query("SELECT bh FROM BarberHairstyle bh WHERE bh.barber.merchant.id = :merchantId AND bh.hairstyleType.id = :hairstyleTypeId AND bh.isDeleted = false")
    List<BarberHairstyle> findByMerchantIdAndHairstyleTypeId(@Param("merchantId") Long merchantId, @Param("hairstyleTypeId") Long hairstyleTypeId);

    @Query("SELECT COUNT(bh) FROM BarberHairstyle bh WHERE bh.barber.id = :barberId AND bh.isDeleted = false")
    Long countByBarberId(@Param("barberId") Long barberId);
}
