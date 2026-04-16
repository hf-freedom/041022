package com.saas.barbershop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BarberLevelResponse {

    private Long id;
    private String name;
    private Integer level;
    private BigDecimal commissionRate;
}
