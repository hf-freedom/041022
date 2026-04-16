package com.saas.barbershop.dto;

import com.saas.barbershop.entity.BarberHairstyle;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BarberMatchResponse {

    private Long barberId;
    private String barberName;
    private String avatarUrl;
    private String barberLevel;
    private BigDecimal rating;
    private Integer orderCount;
    private BarberHairstyle.Proficiency proficiency;
    private String specialties;
    private BigDecimal basePrice;
    private BigDecimal additionalPrice;
    private BigDecimal totalPrice;
    private BigDecimal discountRate;
    private BigDecimal finalPrice;
    private Integer estimatedDuration;
    private Double matchScore;
    private Boolean recommended;
}
