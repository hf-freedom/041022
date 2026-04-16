package com.saas.barbershop.repository;

import com.saas.barbershop.entity.HairstyleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HairstyleTypeRepository extends JpaRepository<HairstyleType, Long> {

    @Query("SELECT h FROM HairstyleType h WHERE h.merchant.id = :merchantId AND h.isDeleted = false")
    List<HairstyleType> findByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT h FROM HairstyleType h WHERE h.merchant.id = :merchantId AND h.isDeleted = false")
    Page<HairstyleType> findByMerchantId(@Param("merchantId") Long merchantId, Pageable pageable);

    @Query("SELECT h FROM HairstyleType h WHERE h.merchant.id = :merchantId AND h.category = :category AND h.isDeleted = false")
    List<HairstyleType> findByMerchantIdAndCategory(@Param("merchantId") Long merchantId, @Param("category") HairstyleType.Category category);

    @Query("SELECT h FROM HairstyleType h WHERE h.merchant.id = :merchantId AND h.isActive = true AND h.isDeleted = false")
    List<HairstyleType> findActiveByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT h FROM HairstyleType h WHERE h.id = :id AND h.merchant.id = :merchantId AND h.isDeleted = false")
    Optional<HairstyleType> findByIdAndMerchantId(@Param("id") Long id, @Param("merchantId") Long merchantId);
}
