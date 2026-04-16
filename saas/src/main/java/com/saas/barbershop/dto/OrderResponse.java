package com.saas.barbershop.dto;

import com.saas.barbershop.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    private Long id;
    private String orderNo;
    private MemberSimpleResponse member;
    private BarberSimpleResponse barber;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal actualAmount;
    private BigDecimal commissionAmount;
    private Order.OrderStatus status;
    private LocalDateTime appointmentTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String remark;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}
