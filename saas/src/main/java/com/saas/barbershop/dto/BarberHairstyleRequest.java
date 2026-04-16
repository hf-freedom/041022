package com.saas.barbershop.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BarberHairstyleRequest {

    @NotNull(message = "理发师ID不能为空")
    private Long barberId;

    @NotNull(message = "发型类型ID不能为空")
    private Long hairstyleTypeId;
}
