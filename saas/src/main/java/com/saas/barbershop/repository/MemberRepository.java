package com.saas.barbershop.repository;

import com.saas.barbershop.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.merchant.id = :merchantId AND m.isDeleted = false")
    List<Member> findByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT m FROM Member m WHERE m.merchant.id = :merchantId AND m.isDeleted = false")
    Page<Member> findByMerchantId(@Param("merchantId") Long merchantId, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.merchant.id = :merchantId AND m.phone = :phone AND m.isDeleted = false")
    Optional<Member> findByMerchantIdAndPhone(@Param("merchantId") Long merchantId, @Param("phone") String phone);

    @Query("SELECT m FROM Member m WHERE m.merchant.id = :merchantId AND (m.name LIKE %:keyword% OR m.phone LIKE %:keyword%) AND m.isDeleted = false")
    Page<Member> findByMerchantIdAndKeyword(@Param("merchantId") Long merchantId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.merchant.id = :merchantId AND m.isDeleted = false")
    Long countByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT m FROM Member m WHERE m.id = :id AND m.merchant.id = :merchantId AND m.isDeleted = false")
    Optional<Member> findByIdAndMerchantId(@Param("id") Long id, @Param("merchantId") Long merchantId);
}
