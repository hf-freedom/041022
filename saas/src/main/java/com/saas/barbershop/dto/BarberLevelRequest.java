package com.saas.barbershop.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BarberLevelRequest {

    @NotBlank(message = "级别名称不能为空")
    private String name;

    @NotNull(message = "级别序号不能为空")
    private Integer level;

    @NotNull(message = "提成比例不能为空")
    @DecimalMin(value = "0.00", message = "提成比例必须在0.00-1.00之间")
    @DecimalMax(value = "1.00", message = "提成比例必须在0.00-1.00之间")
    private BigDecimal commissionRate;

    private String description;

    private Boolean isDefault = false;
}
