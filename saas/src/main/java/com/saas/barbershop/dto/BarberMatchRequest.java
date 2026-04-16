package com.saas.barbershop.dto;

import com.saas.barbershop.entity.Barber;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BarberMatchRequest {

    @NotNull(message = "发型类型ID不能为空")
    private Long hairstyleTypeId;

    private List<Long> hairstyleDetailIds;

    private Long memberId;

    private Barber.Gender preferredGender;
}
