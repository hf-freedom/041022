package com.saas.barbershop.repository;

import com.saas.barbershop.entity.TransactionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {

    @Query("SELECT tr FROM TransactionRecord tr WHERE tr.merchant.id = :merchantId AND tr.isDeleted = false ORDER BY tr.createdAt DESC")
    List<TransactionRecord> findByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT tr FROM TransactionRecord tr WHERE tr.merchant.id = :merchantId AND tr.isDeleted = false ORDER BY tr.createdAt DESC")
    Page<TransactionRecord> findByMerchantId(@Param("merchantId") Long merchantId, Pageable pageable);

    @Query("SELECT tr FROM TransactionRecord tr WHERE tr.merchant.id = :merchantId AND tr.member.id = :memberId AND tr.isDeleted = false ORDER BY tr.createdAt DESC")
    List<TransactionRecord> findByMerchantIdAndMemberId(@Param("merchantId") Long merchantId, @Param("memberId") Long memberId);

    @Query("SELECT tr FROM TransactionRecord tr WHERE tr.merchant.id = :merchantId AND tr.type = :type AND tr.isDeleted = false ORDER BY tr.createdAt DESC")
    List<TransactionRecord> findByMerchantIdAndType(@Param("merchantId") Long merchantId, @Param("type") TransactionRecord.TransactionType type);
}
