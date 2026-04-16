package com.saas.barbershop.dto;

import lombok.Data;

@Data
public class MemberSimpleResponse {

    private Long id;
    private String name;
    private String phone;
}
