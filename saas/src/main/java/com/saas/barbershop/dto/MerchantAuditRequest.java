package com.saas.barbershop.dto;

import com.saas.barbershop.entity.Merchant;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MerchantAuditRequest {

    @NotNull(message = "审核状态不能为空")
    private Merchant.MerchantStatus status;

    @Size(max = 500, message = "审核备注不能超过500个字符")
    private String auditRemark;
}
