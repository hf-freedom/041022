package com.saas.barbershop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@Setter
public class Member extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "gender", length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "total_consumption", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalConsumption = BigDecimal.ZERO;

    @Column(name = "total_recharge", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalRecharge = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private MemberLevel memberLevel;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "last_consumption_at")
    private LocalDateTime lastConsumptionAt;

    public enum Gender {
        MALE, FEMALE, UNKNOWN
    }
}
