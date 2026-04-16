package com.barbershop.saas.entity;

import com.barbershop.saas.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_member")
public class Member extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    private String name;
    private String phone;
    private String gender;
    private Integer age;
    private BigDecimal balance;
    private Integer totalConsumeCount;
    private BigDecimal totalConsumeAmount;
    private Integer levelId;
    private String levelName;
    private BigDecimal discount;
}
