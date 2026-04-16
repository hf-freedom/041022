package com.saas.barbershop.repository;

import com.saas.barbershop.entity.Barber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BarberRepository extends JpaRepository<Barber, Long> {

    @Query("SELECT b FROM Barber b WHERE b.merchant.id = :merchantId AND b.isDeleted = false")
    List<Barber> findByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT b FROM Barber b WHERE b.merchant.id = :merchantId AND b.isDeleted = false")
    Page<Barber> findByMerchantId(@Param("merchantId") Long merchantId, Pageable pageable);

    @Query("SELECT b FROM Barber b WHERE b.merchant.id = :merchantId AND b.isActive = true AND b.isDeleted = false")
    List<Barber> findActiveByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT b FROM Barber b WHERE b.merchant.id = :merchantId AND (b.name LIKE %:keyword% OR b.phone LIKE %:keyword%) AND b.isDeleted = false")
    Page<Barber> findByMerchantIdAndKeyword(@Param("merchantId") Long merchantId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Barber b WHERE b.id = :id AND b.merchant.id = :merchantId AND b.isDeleted = false")
    Optional<Barber> findByIdAndMerchantId(@Param("id") Long id, @Param("merchantId") Long merchantId);

    @Query("SELECT COUNT(b) FROM Barber b WHERE b.merchant.id = :merchantId AND b.isDeleted = false")
    Long countByMerchantId(@Param("merchantId") Long merchantId);
}
