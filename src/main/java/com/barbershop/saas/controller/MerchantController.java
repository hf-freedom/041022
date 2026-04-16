package com.barbershop.saas.controller;

import com.barbershop.saas.common.Result;
import com.barbershop.saas.dto.MerchantLoginDTO;
import com.barbershop.saas.dto.MerchantRegisterDTO;
import com.barbershop.saas.entity.Merchant;
import com.barbershop.saas.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody MerchantRegisterDTO dto) {
        merchantService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody MerchantLoginDTO dto) {
        Map<String, Object> result = merchantService.login(dto);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Merchant> getById(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        merchant.setPassword(null);
        return Result.success(merchant);
    }
}
