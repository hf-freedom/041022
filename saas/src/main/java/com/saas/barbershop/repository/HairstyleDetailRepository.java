package com.saas.barbershop.repository;

import com.saas.barbershop.entity.HairstyleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HairstyleDetailRepository extends JpaRepository<HairstyleDetail, Long> {

    @Query("SELECT hd FROM HairstyleDetail hd WHERE hd.hairstyleType.id = :hairstyleTypeId AND hd.isDeleted = false")
    List<HairstyleDetail> findByHairstyleTypeId(@Param("hairstyleTypeId") Long hairstyleTypeId);

    @Query("SELECT hd FROM HairstyleDetail hd WHERE hd.hairstyleType.id = :hairstyleTypeId AND hd.isActive = true AND hd.isDeleted = false")
    List<HairstyleDetail> findActiveByHairstyleTypeId(@Param("hairstyleTypeId") Long hairstyleTypeId);

    @Query("SELECT hd FROM HairstyleDetail hd WHERE hd.id = :id AND hd.hairstyleType.id = :hairstyleTypeId AND hd.isDeleted = false")
    Optional<HairstyleDetail> findByIdAndHairstyleTypeId(@Param("id") Long id, @Param("hairstyleTypeId") Long hairstyleTypeId);
}
