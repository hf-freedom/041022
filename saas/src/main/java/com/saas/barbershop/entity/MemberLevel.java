package com.saas.barbershop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "member_levels")
@Getter
@Setter
public class MemberLevel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "min_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal minAmount;

    @Column(name = "discount_rate", nullable = false, precision = 3, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
}
