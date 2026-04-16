package com.saas.barbershop.controller;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/merchants")
@Tag(name = "商家管理(管理员)", description = "管理员审核、管理商家接口")
@PreAuthorize("hasRole('ADMIN')")
public class MerchantAdminController {

    @Autowired
    private MerchantService merchantService;

    @GetMapping
    @Operation(summary = "获取所有商家", description = "分页获取所有商家列表")
    public ResponseEntity<ApiResponse<Page<MerchantResponse>>> getAllMerchants(Pageable pageable) {
        Page<MerchantResponse> response = merchantService.getAllMerchants(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态获取商家", description = "根据审核状态获取商家列表")
    public ResponseEntity<ApiResponse<Page<MerchantResponse>>> getMerchantsByStatus(
            @Parameter(description = "状态: PENDING-待审核, APPROVED-已通过, REJECTED-已拒绝, DISABLED-已禁用")
            @PathVariable Merchant.MerchantStatus status,
            Pageable pageable) {
        Page<MerchantResponse> response = merchantService.getMerchantsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索商家", description = "根据关键词搜索商家")
    public ResponseEntity<ApiResponse<Page<MerchantResponse>>> searchMerchants(
            @RequestParam String keyword,
            Pageable pageable) {
        Page<MerchantResponse> response = merchantService.searchMerchants(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/audit")
    @Operation(summary = "审核商家", description = "审核商家入驻申请")
    public ResponseEntity<ApiResponse<MerchantResponse>> auditMerchant(
            @PathVariable Long id,
            @Valid @RequestBody MerchantAuditRequest request) {
        MerchantResponse response = merchantService.auditMerchant(id, request);
        return ResponseEntity.ok(ApiResponse.success("审核完成", response));
    }

    @PostMapping("/{id}/disable")
    @Operation(summary = "禁用商家", description = "禁用商家账号")
    public ResponseEntity<ApiResponse<Void>> disableMerchant(@PathVariable Long id) {
        merchantService.disableMerchant(id);
        return ResponseEntity.ok(ApiResponse.success("商家已禁用", null));
    }

    @PostMapping("/{id}/enable")
    @Operation(summary = "启用商家", description = "启用商家账号")
    public ResponseEntity<ApiResponse<Void>> enableMerchant(@PathVariable Long id) {
        merchantService.enableMerchant(id);
        return ResponseEntity.ok(ApiResponse.success("商家已启用", null));
    }
}
