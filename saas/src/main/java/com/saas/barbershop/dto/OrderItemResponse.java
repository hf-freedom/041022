package com.saas.barbershop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderItemResponse {

    private Long id;
    private Long hairstyleTypeId;
    private String hairstyleName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private List<OrderItemDetailResponse> details;
}
