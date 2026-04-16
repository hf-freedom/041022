package com.barbershop.saas.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderDTO {
    private Long memberId;
    @NotNull(message = "理发师ID不能为空")
    private Long barberId;
    @NotNull(message = "发型ID不能为空")
    private Long hairstyleId;
    private List<String> detailOptions;
    private Integer payType;
    private Integer useBalance;
    private String remark;
}
