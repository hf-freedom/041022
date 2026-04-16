package com.saas.barbershop.dto;

import com.saas.barbershop.entity.HairstyleType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HairstyleTypeResponse {

    private Long id;
    private String name;
    private HairstyleType.Category category;
    private BigDecimal basePrice;
    private Integer durationMinutes;
    private String imageUrl;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
