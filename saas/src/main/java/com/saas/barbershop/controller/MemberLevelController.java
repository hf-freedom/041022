package com.saas.barbershop.controller;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.service.MemberLevelService;
import com.saas.barbershop.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/member-levels")
@Tag(name = "会员等级管理", description = "会员等级CRUD接口")
public class MemberLevelController {

    @Autowired
    private MemberLevelService memberLevelService;

    @PostMapping
    @Operation(summary = "创建会员等级", description = "创建新的会员等级")
    public ResponseEntity<ApiResponse<MemberLevelResponse>> createLevel(@Valid @RequestBody MemberLevelRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        MemberLevelResponse response = memberLevelService.createLevel(merchantId, request);
        return ResponseEntity.ok(ApiResponse.success("会员等级创建成功", response));
    }

    @GetMapping
    @Operation(summary = "获取会员等级列表", description = "获取所有会员等级")
    public ResponseEntity<ApiResponse<List<MemberLevelResponse>>> getAllLevels() {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<MemberLevelResponse> response = memberLevelService.getAllLevels(merchantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取会员等级详情", description = "根据ID获取会员等级")
    public ResponseEntity<ApiResponse<MemberLevelResponse>> getLevel(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        MemberLevelResponse response = memberLevelService.getLevel(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新会员等级", description = "更新会员等级信息")
    public ResponseEntity<ApiResponse<MemberLevelResponse>> updateLevel(
            @PathVariable Long id,
            @Valid @RequestBody MemberLevelRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        MemberLevelResponse response = memberLevelService.updateLevel(merchantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("会员等级更新成功", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除会员等级", description = "删除会员等级")
    public ResponseEntity<ApiResponse<Void>> deleteLevel(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        memberLevelService.deleteLevel(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success("会员等级已删除", null));
    }
}
