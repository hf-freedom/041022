package com.saas.barbershop.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MemberLevelRequest {

    @NotBlank(message = "等级名称不能为空")
    private String name;

    @NotNull(message = "等级序号不能为空")
    private Integer level;

    @NotNull(message = "最低消费金额不能为空")
    @DecimalMin(value = "0.00", message = "最低消费金额不能小于0")
    private BigDecimal minAmount;

    @NotNull(message = "折扣率不能为空")
    @DecimalMin(value = "0.01", message = "折扣率必须在0.01-1.00之间")
    @DecimalMax(value = "1.00", message = "折扣率必须在0.01-1.00之间")
    private BigDecimal discountRate;

    private String description;

    private Boolean isDefault = false;
}
