package com.saas.barbershop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "barbers")
@Getter
@Setter
public class Barber extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "gender", length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id")
    private BarberLevel barberLevel;

    @Column(name = "specialties", length = 500)
    private String specialties;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "order_count", nullable = false)
    private Integer orderCount = 0;

    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating = new BigDecimal("5.0");

    public enum Gender {
        MALE, FEMALE
    }
}
