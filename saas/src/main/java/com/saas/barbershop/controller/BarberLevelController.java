package com.saas.barbershop.controller;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.service.BarberLevelService;
import com.saas.barbershop.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/barber-levels")
@Tag(name = "理发师级别管理", description = "理发师级别CRUD接口")
public class BarberLevelController {

    @Autowired
    private BarberLevelService barberLevelService;

    @PostMapping
    @Operation(summary = "创建理发师级别", description = "创建新的理发师级别")
    public ResponseEntity<ApiResponse<BarberLevelResponse>> createLevel(@Valid @RequestBody BarberLevelRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        BarberLevelResponse response = barberLevelService.createLevel(merchantId, request);
        return ResponseEntity.ok(ApiResponse.success("理发师级别创建成功", response));
    }

    @GetMapping
    @Operation(summary = "获取理发师级别列表", description = "获取所有理发师级别")
    public ResponseEntity<ApiResponse<List<BarberLevelResponse>>> getAllLevels() {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<BarberLevelResponse> response = barberLevelService.getAllLevels(merchantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取理发师级别详情", description = "根据ID获取理发师级别")
    public ResponseEntity<ApiResponse<BarberLevelResponse>> getLevel(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        BarberLevelResponse response = barberLevelService.getLevel(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新理发师级别", description = "更新理发师级别信息")
    public ResponseEntity<ApiResponse<BarberLevelResponse>> updateLevel(
            @PathVariable Long id,
            @Valid @RequestBody BarberLevelRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        BarberLevelResponse response = barberLevelService.updateLevel(merchantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("理发师级别更新成功", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除理发师级别", description = "删除理发师级别")
    public ResponseEntity<ApiResponse<Void>> deleteLevel(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        barberLevelService.deleteLevel(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success("理发师级别已删除", null));
    }
}
