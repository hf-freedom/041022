package com.saas.barbershop.repository;

import com.saas.barbershop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.merchant.id = :merchantId AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<Order> findByMerchantId(@Param("merchantId") Long merchantId);

    @Query("SELECT o FROM Order o WHERE o.merchant.id = :merchantId AND o.isDeleted = false ORDER BY o.createdAt DESC")
    Page<Order> findByMerchantId(@Param("merchantId") Long merchantId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.merchant.id = :merchantId AND o.member.id = :memberId AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<Order> findByMerchantIdAndMemberId(@Param("merchantId") Long merchantId, @Param("memberId") Long memberId);

    @Query("SELECT o FROM Order o WHERE o.merchant.id = :merchantId AND o.barber.id = :barberId AND o.isDeleted = false ORDER BY o.createdAt DESC")
    List<Order> findByMerchantIdAndBarberId(@Param("merchantId") Long merchantId, @Param("barberId") Long barberId);

    @Query("SELECT o FROM Order o WHERE o.merchant.id = :merchantId AND o.status = :status AND o.isDeleted = false ORDER BY o.createdAt DESC")
    Page<Order> findByMerchantIdAndStatus(@Param("merchantId") Long merchantId, @Param("status") Order.OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.merchant.id = :merchantId AND o.isDeleted = false")
    Optional<Order> findByIdAndMerchantId(@Param("id") Long id, @Param("merchantId") Long merchantId);

    @Query("SELECT o FROM Order o WHERE o.merchant.id = :merchantId AND o.appointmentTime BETWEEN :startTime AND :endTime AND o.isDeleted = false")
    List<Order> findByMerchantIdAndAppointmentTimeBetween(@Param("merchantId") Long merchantId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT o FROM Order o WHERE o.merchant.id = :merchantId AND o.barber.id = :barberId AND o.status = :status AND o.isDeleted = false")
    List<Order> findByMerchantIdAndBarberIdAndStatus(@Param("merchantId") Long merchantId, @Param("barberId") Long barberId, @Param("status") Order.OrderStatus status);
}
