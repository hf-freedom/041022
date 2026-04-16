package com.saas.barbershop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HairstyleDetailResponse {

    private Long id;
    private Long hairstyleTypeId;
    private String name;
    private BigDecimal additionalPrice;
    private Integer additionalMinutes;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
