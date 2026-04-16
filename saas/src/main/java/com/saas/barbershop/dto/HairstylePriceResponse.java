package com.saas.barbershop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HairstylePriceResponse {

    private Long hairstyleTypeId;
    private String hairstyleName;
    private BigDecimal basePrice;
    private BigDecimal additionalPrice;
    private BigDecimal totalPrice;
    private BigDecimal discountRate;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
    private Integer baseDuration;
    private Integer additionalDuration;
    private Integer totalDuration;
    private List<HairstyleDetailResponse> selectedDetails;
}
