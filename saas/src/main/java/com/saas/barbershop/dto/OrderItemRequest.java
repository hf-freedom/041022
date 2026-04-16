package com.saas.barbershop.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class OrderItemRequest {

    @NotNull(message = "发型类型ID不能为空")
    private Long hairstyleTypeId;

    private List<Long> hairstyleDetailIds;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为1")
    private Integer quantity;
}
