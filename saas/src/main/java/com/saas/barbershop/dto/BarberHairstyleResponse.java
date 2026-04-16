package com.saas.barbershop.dto;

import com.saas.barbershop.entity.BarberHairstyle;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BarberHairstyleResponse {

    private Long id;
    private Long barberId;
    private String barberName;
    private Long hairstyleTypeId;
    private String hairstyleName;
    private BarberHairstyle.Proficiency proficiency;
    private LocalDateTime createdAt;
}
