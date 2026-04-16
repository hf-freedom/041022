package com.barbershop.saas.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class MatchBarberDTO {
    @NotNull(message = "发型ID不能为空")
    private Long hairstyleId;
    private List<String> detailOptions;
}
