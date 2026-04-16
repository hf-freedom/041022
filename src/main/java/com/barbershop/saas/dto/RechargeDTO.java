package com.barbershop.saas.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class RechargeDTO {
    @NotNull(message = "会员ID不能为空")
    private Long memberId;
    @NotNull(message = "充值金额不能为空")
    private BigDecimal amount;
    private String rechargeType;
    private String remark;
}
