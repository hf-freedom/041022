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
@TableName("t_order")
public class Order extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    private String orderNo;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private Long barberId;
    private String barberName;
    private Long hairstyleId;
    private String hairstyleName;
    private String detailOptions;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal actualPrice;
    private Integer payType;
    private Integer useBalance;
    private BigDecimal balanceAmount;
    private Integer status;
    private BigDecimal commissionRate;
    private BigDecimal commissionAmount;
    private String remark;
}
