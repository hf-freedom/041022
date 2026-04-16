package com.saas.barbershop.dto;

import com.saas.barbershop.entity.HairstyleType;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class HairstyleTypeRequest {

    @NotBlank(message = "发型名称不能为空")
    private String name;

    @NotNull(message = "发型分类不能为空")
    private HairstyleType.Category category;

    @NotNull(message = "基础价格不能为空")
    @DecimalMin(value = "0.00", message = "基础价格不能小于0")
    private BigDecimal basePrice;

    @NotNull(message = "预计时长不能为空")
    @Min(value = 1, message = "预计时长至少1分钟")
    private Integer durationMinutes;

    private String imageUrl;

    private String description;

    private Boolean isActive = true;
}
