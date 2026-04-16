package com.saas.barbershop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "barber_hairstyles")
@Getter
@Setter
public class BarberHairstyle extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barber_id", nullable = false)
    private Barber barber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hairstyle_type_id", nullable = false)
    private HairstyleType hairstyleType;

    @Column(name = "proficiency", nullable = false)
    @Enumerated(EnumType.STRING)
    private Proficiency proficiency = Proficiency.MEDIUM;

    public enum Proficiency {
        BEGINNER,   // 初级
        MEDIUM,     // 中级
        ADVANCED,   // 高级
        EXPERT      // 专家
    }
}
