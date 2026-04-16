package com.barbershop.saas.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class HairstyleDTO {
    @NotBlank(message = "发型名称不能为空")
    private String name;
    @NotBlank(message = "分类不能为空")
    private String category;
    private String image;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private String suitableGender;
    private String suitableAge;
    private String detailOptions;
    private String skillTags;
    private Integer status;
}
