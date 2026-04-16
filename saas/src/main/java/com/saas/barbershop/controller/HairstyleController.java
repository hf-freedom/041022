package com.saas.barbershop.controller;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.HairstyleType;
import com.saas.barbershop.service.HairstyleService;
import com.saas.barbershop.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/hairstyles")
@Tag(name = "发型管理", description = "发型类型和细节管理接口")
public class HairstyleController {

    @Autowired
    private HairstyleService hairstyleService;

    @PostMapping("/types")
    @Operation(summary = "创建发型类型", description = "创建新的发型类型")
    public ResponseEntity<ApiResponse<HairstyleTypeResponse>> createHairstyleType(
            @Valid @RequestBody HairstyleTypeRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        HairstyleTypeResponse response = hairstyleService.createHairstyleType(merchantId, request);
        return ResponseEntity.ok(ApiResponse.success("发型类型创建成功", response));
    }

    @GetMapping("/types")
    @Operation(summary = "获取发型类型列表", description = "分页获取发型类型列表")
    public ResponseEntity<ApiResponse<Page<HairstyleTypeResponse>>> getHairstyleTypes(Pageable pageable) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        Page<HairstyleTypeResponse> response = hairstyleService.getHairstyleTypes(merchantId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/types/all")
    @Operation(summary = "获取所有发型类型", description = "获取所有发型类型")
    public ResponseEntity<ApiResponse<List<HairstyleTypeResponse>>> getAllHairstyleTypes() {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<HairstyleTypeResponse> response = hairstyleService.getAllHairstyleTypes(merchantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/types/active")
    @Operation(summary = "获取启用的发型类型", description = "获取所有启用的发型类型")
    public ResponseEntity<ApiResponse<List<HairstyleTypeResponse>>> getActiveHairstyleTypes() {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<HairstyleTypeResponse> response = hairstyleService.getActiveHairstyleTypes(merchantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/types/category/{category}")
    @Operation(summary = "按分类获取发型类型", description = "根据分类获取发型类型")
    public ResponseEntity<ApiResponse<List<HairstyleTypeResponse>>> getHairstyleTypesByCategory(
            @Parameter(description = "分类: CUT-剪发, PERM-烫发, COLOR-染发, CARE-护理, STYLING-造型")
            @PathVariable HairstyleType.Category category) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<HairstyleTypeResponse> response = hairstyleService.getHairstyleTypesByCategory(merchantId, category);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/types/{id}")
    @Operation(summary = "获取发型类型详情", description = "根据ID获取发型类型")
    public ResponseEntity<ApiResponse<HairstyleTypeResponse>> getHairstyleType(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        HairstyleTypeResponse response = hairstyleService.getHairstyleType(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/types/{id}")
    @Operation(summary = "更新发型类型", description = "更新发型类型信息")
    public ResponseEntity<ApiResponse<HairstyleTypeResponse>> updateHairstyleType(
            @PathVariable Long id,
            @Valid @RequestBody HairstyleTypeRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        HairstyleTypeResponse response = hairstyleService.updateHairstyleType(merchantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("发型类型更新成功", response));
    }

    @DeleteMapping("/types/{id}")
    @Operation(summary = "删除发型类型", description = "删除发型类型")
    public ResponseEntity<ApiResponse<Void>> deleteHairstyleType(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        hairstyleService.deleteHairstyleType(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success("发型类型已删除", null));
    }

    @PostMapping("/types/{typeId}/details")
    @Operation(summary = "创建发型细节", description = "为发型类型添加细节选项")
    public ResponseEntity<ApiResponse<HairstyleDetailResponse>> createHairstyleDetail(
            @PathVariable Long typeId,
            @Valid @RequestBody HairstyleDetailRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        HairstyleDetailResponse response = hairstyleService.createHairstyleDetail(merchantId, typeId, request);
        return ResponseEntity.ok(ApiResponse.success("发型细节创建成功", response));
    }

    @GetMapping("/types/{typeId}/details")
    @Operation(summary = "获取发型细节列表", description = "获取发型类型的所有细节")
    public ResponseEntity<ApiResponse<List<HairstyleDetailResponse>>> getHairstyleDetails(
            @PathVariable Long typeId) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<HairstyleDetailResponse> response = hairstyleService.getHairstyleDetails(merchantId, typeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/types/{typeId}/details/active")
    @Operation(summary = "获取启用的发型细节", description = "获取发型类型的启用细节")
    public ResponseEntity<ApiResponse<List<HairstyleDetailResponse>>> getActiveHairstyleDetails(
            @PathVariable Long typeId) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<HairstyleDetailResponse> response = hairstyleService.getActiveHairstyleDetails(merchantId, typeId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/types/{typeId}/details/{detailId}")
    @Operation(summary = "更新发型细节", description = "更新发型细节信息")
    public ResponseEntity<ApiResponse<HairstyleDetailResponse>> updateHairstyleDetail(
            @PathVariable Long typeId,
            @PathVariable Long detailId,
            @Valid @RequestBody HairstyleDetailRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        HairstyleDetailResponse response = hairstyleService.updateHairstyleDetail(merchantId, typeId, detailId, request);
        return ResponseEntity.ok(ApiResponse.success("发型细节更新成功", response));
    }

    @DeleteMapping("/types/{typeId}/details/{detailId}")
    @Operation(summary = "删除发型细节", description = "删除发型细节")
    public ResponseEntity<ApiResponse<Void>> deleteHairstyleDetail(
            @PathVariable Long typeId,
            @PathVariable Long detailId) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        hairstyleService.deleteHairstyleDetail(merchantId, typeId, detailId);
        return ResponseEntity.ok(ApiResponse.success("发型细节已删除", null));
    }
}
