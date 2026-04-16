package com.barbershop.saas.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MerchantRegisterDTO {
    @NotBlank(message = "店铺名称不能为空")
    private String shopName;
    @NotBlank(message = "联系人不能为空")
    private String contactName;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    private String email;
    @NotBlank(message = "地址不能为空")
    private String address;
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    private String businessLicense;
    private String idCardFront;
    private String idCardBack;
}
