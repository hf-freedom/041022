package com.saas.barbershop.repository;

import com.saas.barbershop.entity.MemberLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberLevelRepository extends JpaRepository<MemberLevel, Long> {

    @Query("SELECT ml FROM MemberLevel ml WHERE ml.merchant.id = :merchantId AND ml.isDeleted = false ORDER BY ml.level ASC")
    List<MemberLevel> findByMerchantIdOrderByLevelAsc(@Param("merchantId") Long merchantId);

    @Query("SELECT ml FROM MemberLevel ml WHERE ml.merchant.id = :merchantId AND ml.isDefault = true AND ml.isDeleted = false")
    Optional<MemberLevel> findDefaultByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT ml FROM MemberLevel ml WHERE ml.id = :id AND ml.merchant.id = :merchantId AND ml.isDeleted = false")
    Optional<MemberLevel> findByIdAndMerchantId(@Param("id") Long id, @Param("merchantId") Long merchantId);

    @Query("SELECT COUNT(ml) FROM MemberLevel ml WHERE ml.merchant.id = :merchantId AND ml.isDeleted = false")
    Long countByMerchantId(@Param("merchantId") Long merchantId);
}
