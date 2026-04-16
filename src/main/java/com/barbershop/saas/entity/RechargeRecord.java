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
@TableName("t_recharge_record")
public class RechargeRecord extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long merchantId;
    private Long memberId;
    private String memberName;
    private String memberPhone;
    private BigDecimal amount;
    private BigDecimal beforeBalance;
    private BigDecimal afterBalance;
    private String rechargeType;
    private String remark;
    private Long operatorId;
    private String operatorName;
}
