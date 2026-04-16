package com.saas.barbershop.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class HairstyleDetailRequest {

    @NotBlank(message = "细节名称不能为空")
    private String name;

    @NotNull(message = "额外价格不能为空")
    @DecimalMin(value = "0.00", message = "额外价格不能小于0")
    private BigDecimal additionalPrice;

    @NotNull(message = "额外时长不能为空")
    @Min(value = 0, message = "额外时长不能小于0")
    private Integer additionalMinutes;

    private String description;

    private Boolean isActive = true;
}
