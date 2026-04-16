package com.saas.barbershop.dto;

import com.saas.barbershop.entity.Barber;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BarberResponse {

    private Long id;
    private String name;
    private String phone;
    private String avatarUrl;
    private Barber.Gender gender;
    private LocalDate birthday;
    private LocalDate entryDate;
    private BarberLevelResponse barberLevel;
    private String specialties;
    private String introduction;
    private Boolean isActive;
    private Integer orderCount;
    private BigDecimal rating;
    private LocalDateTime createdAt;
}
