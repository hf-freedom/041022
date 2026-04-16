package com.saas.barbershop.repository;

import com.saas.barbershop.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    Optional<Merchant> findByPhone(String phone);

    boolean existsByPhone(String phone);

    @Query("SELECT m FROM Merchant m WHERE m.isDeleted = false AND m.status = :status")
    Page<Merchant> findByStatus(@Param("status") Merchant.MerchantStatus status, Pageable pageable);

    @Query("SELECT m FROM Merchant m WHERE m.isDeleted = false AND (m.shopName LIKE %:keyword% OR m.ownerName LIKE %:keyword% OR m.phone LIKE %:keyword%)")
    Page<Merchant> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Merchant m WHERE m.isDeleted = false")
    Page<Merchant> findAllActive(Pageable pageable);

    Optional<Merchant> findByIdAndIsDeletedFalse(Long id);
}
