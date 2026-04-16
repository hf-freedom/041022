package com.barbershop.saas.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BarberDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    private String avatar;
    @NotNull(message = "级别ID不能为空")
    private Integer levelId;
    private String skillTags;
    private Integer status;
}
