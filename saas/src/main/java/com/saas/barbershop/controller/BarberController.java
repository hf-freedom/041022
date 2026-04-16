package com.saas.barbershop.controller;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.service.BarberService;
import com.saas.barbershop.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/barbers")
@Tag(name = "理发师管理", description = "理发师CRUD接口")
public class BarberController {

    @Autowired
    private BarberService barberService;

    @PostMapping
    @Operation(summary = "创建理发师", description = "添加新理发师")
    public ResponseEntity<ApiResponse<BarberResponse>> createBarber(@Valid @RequestBody BarberCreateRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        BarberResponse response = barberService.createBarber(merchantId, request);
        return ResponseEntity.ok(ApiResponse.success("理发师创建成功", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取理发师详情", description = "根据ID获取理发师信息")
    public ResponseEntity<ApiResponse<BarberResponse>> getBarber(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        BarberResponse response = barberService.getBarber(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "获取理发师列表", description = "分页获取理发师列表")
    public ResponseEntity<ApiResponse<Page<BarberResponse>>> getBarbers(Pageable pageable) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        Page<BarberResponse> response = barberService.getBarbers(merchantId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有理发师", description = "获取所有理发师列表")
    public ResponseEntity<ApiResponse<List<BarberResponse>>> getAllBarbers() {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<BarberResponse> response = barberService.getAllBarbers(merchantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @Operation(summary = "获取在职理发师", description = "获取所有在职理发师")
    public ResponseEntity<ApiResponse<List<BarberResponse>>> getActiveBarbers() {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<BarberResponse> response = barberService.getActiveBarbers(merchantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索理发师", description = "根据姓名或手机号搜索理发师")
    public ResponseEntity<ApiResponse<Page<BarberResponse>>> searchBarbers(
            @RequestParam String keyword,
            Pageable pageable) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        Page<BarberResponse> response = barberService.searchBarbers(merchantId, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新理发师", description = "更新理发师信息")
    public ResponseEntity<ApiResponse<BarberResponse>> updateBarber(
            @PathVariable Long id,
            @Valid @RequestBody BarberUpdateRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        BarberResponse response = barberService.updateBarber(merchantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("理发师信息更新成功", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除理发师", description = "删除理发师（逻辑删除）")
    public ResponseEntity<ApiResponse<Void>> deleteBarber(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        barberService.deleteBarber(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success("理发师已删除", null));
    }
}
