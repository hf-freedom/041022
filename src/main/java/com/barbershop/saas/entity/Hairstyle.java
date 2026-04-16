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
@TableName("t_hairstyle")
public class Hairstyle extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    private String name;
    private String category;
    private String image;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private String suitableGender;
    private String suitableAge;
    private String detailOptions;
    private String skillTags;
    private Integer status;
}
