package com.saas.barbershop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "merchants")
@Getter
@Setter
public class Merchant extends BaseEntity {

    @Column(name = "shop_name", nullable = false, length = 100)
    private String shopName;

    @Column(name = "owner_name", nullable = false, length = 50)
    private String ownerName;

    @Column(name = "phone", nullable = false, length = 20, unique = true)
    private String phone;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "business_license", length = 100)
    private String businessLicense;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MerchantStatus status = MerchantStatus.PENDING;

    @Column(name = "audit_remark", length = 500)
    private String auditRemark;

    @Column(name = "audited_at")
    private LocalDateTime auditedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public enum MerchantStatus {
        PENDING,    // 待审核
        APPROVED,   // 已通过
        REJECTED,   // 已拒绝
        DISABLED    // 已禁用
    }
}
