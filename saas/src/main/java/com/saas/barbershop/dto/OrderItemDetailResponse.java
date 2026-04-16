package com.saas.barbershop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDetailResponse {

    private Long id;
    private String detailName;
    private BigDecimal additionalPrice;
}
