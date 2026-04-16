package com.saas.barbershop.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderCreateRequest {

    @NotNull(message = "理发师ID不能为空")
    private Long barberId;

    private Long memberId;

    @NotEmpty(message = "订单项目不能为空")
    @Valid
    private List<OrderItemRequest> items;

    private LocalDateTime appointmentTime;

    private String remark;
}
