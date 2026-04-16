package com.saas.barbershop.dto;

import com.saas.barbershop.entity.Member;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class MemberUpdateRequest {

    @NotBlank(message = "会员姓名不能为空")
    @Size(max = 50, message = "会员姓名不能超过50个字符")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private Member.Gender gender;

    private LocalDate birthday;

    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
