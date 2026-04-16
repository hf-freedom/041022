package com.saas.barbershop.dto;

import com.saas.barbershop.entity.Member;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MemberResponse {

    private Long id;
    private String name;
    private String phone;
    private Member.Gender gender;
    private LocalDate birthday;
    private String avatarUrl;
    private BigDecimal balance;
    private BigDecimal totalConsumption;
    private BigDecimal totalRecharge;
    private MemberLevelResponse memberLevel;
    private String remark;
    private LocalDateTime lastConsumptionAt;
    private LocalDateTime createdAt;
}
