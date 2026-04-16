package com.saas.barbershop.controller;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.service.MerchantService;
import com.saas.barbershop.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "商家注册、登录等接口")
public class AuthController {

    @Autowired
    private MerchantService merchantService;

    @PostMapping("/register")
    @Operation(summary = "商家注册", description = "新商家申请入驻平台")
    public ResponseEntity<ApiResponse<MerchantResponse>> register(@Valid @RequestBody MerchantRegisterRequest request) {
        MerchantResponse response = merchantService.register(request);
        return ResponseEntity.ok(ApiResponse.success("注册成功，等待审核", response));
    }

    @PostMapping("/login")
    @Operation(summary = "商家登录", description = "商家使用手机号和密码登录")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody MerchantLoginRequest request) {
        LoginResponse response = merchantService.login(request);
        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前商家信息", description = "获取当前登录商家的详细信息")
    public ResponseEntity<ApiResponse<MerchantResponse>> getCurrentMerchant() {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        MerchantResponse response = merchantService.getCurrentMerchant(merchantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/me")
    @Operation(summary = "更新商家信息", description = "更新当前登录商家的信息")
    public ResponseEntity<ApiResponse<MerchantResponse>> updateMerchant(@Valid @RequestBody MerchantRegisterRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        MerchantResponse response = merchantService.updateMerchant(merchantId, request);
        return ResponseEntity.ok(ApiResponse.success("更新成功", response));
    }
}
