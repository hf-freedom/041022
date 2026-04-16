package com.saas.barbershop.dto;

import com.saas.barbershop.entity.Barber;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class BarberCreateRequest {

    @NotBlank(message = "理发师姓名不能为空")
    @Size(max = 50, message = "理发师姓名不能超过50个字符")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private Barber.Gender gender;

    private LocalDate birthday;

    private LocalDate entryDate;

    private Long levelId;

    @Size(max = 500, message = "专长描述不能超过500个字符")
    private String specialties;

    @Size(max = 2000, message = "个人介绍不能超过2000个字符")
    private String introduction;
}
