package com.saas.barbershop.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class MerchantRegisterRequest {

    @NotBlank(message = "店铺名称不能为空")
    @Size(max = 100, message = "店铺名称不能超过100个字符")
    private String shopName;

    @NotBlank(message = "负责人姓名不能为空")
    @Size(max = 50, message = "负责人姓名不能超过50个字符")
    private String ownerName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String password;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    @Size(max = 255, message = "地址不能超过255个字符")
    private String address;

    @Size(max = 100, message = "营业执照号不能超过100个字符")
    private String businessLicense;

    @Size(max = 500, message = "店铺描述不能超过500个字符")
    private String description;
}
