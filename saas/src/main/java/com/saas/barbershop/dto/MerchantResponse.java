package com.saas.barbershop.dto;

import com.saas.barbershop.entity.Merchant;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantResponse {

    private Long id;
    private String shopName;
    private String ownerName;
    private String phone;
    private String email;
    private String address;
    private String businessLicense;
    private String logoUrl;
    private String description;
    private Merchant.MerchantStatus status;
    private String auditRemark;
    private LocalDateTime auditedAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
