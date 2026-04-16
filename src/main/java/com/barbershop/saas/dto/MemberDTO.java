package com.barbershop.saas.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class MemberDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;
    @NotBlank(message = "手机号不能为空")
    private String phone;
    private String gender;
    private Integer age;
    private BigDecimal balance;
}
