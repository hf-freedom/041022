package com.barbershop.saas.entity;

import com.barbershop.saas.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_merchant")
public class Merchant extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String shopName;
    private String contactName;
    private String phone;
    private String email;
    private String address;
    private String username;
    private String password;
    private String businessLicense;
    private String idCardFront;
    private String idCardBack;
    private Integer status;
    private String auditRemark;
    private Integer online;
}
