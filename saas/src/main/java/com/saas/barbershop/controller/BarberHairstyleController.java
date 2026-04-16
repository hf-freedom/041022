package com.saas.barbershop.controller;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.BarberHairstyle;
import com.saas.barbershop.service.BarberHairstyleService;
import com.saas.barbershop.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/barber-hairstyles")
@Tag(name = "理发师发型关联", description = "管理理发师擅长的发型")
public class BarberHairstyleController {

    @Autowired
    private BarberHairstyleService barberHairstyleService;

    @PostMapping
    @Operation(summary = "添加理发师发型", description = "为理发师添加擅长的发型")
    public ResponseEntity<ApiResponse<BarberHairstyleResponse>> addBarberHairstyle(
            @Valid @RequestBody BarberHairstyleRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        BarberHairstyleResponse response = barberHairstyleService.addBarberHairstyle(merchantId, request);
        return ResponseEntity.ok(ApiResponse.success("添加成功", response));
    }

    @GetMapping("/barber/{barberId}")
    @Operation(summary = "获取理发师的发型", description = "获取理发师擅长的所有发型")
    public ResponseEntity<ApiResponse<List<BarberHairstyleResponse>>> getBarberHairstyles(
            @PathVariable Long barberId) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<BarberHairstyleResponse> response = barberHairstyleService.getBarberHairstyles(merchantId, barberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新熟练度", description = "更新理发师对某发型的熟练度")
    public ResponseEntity<ApiResponse<BarberHairstyleResponse>> updateProficiency(
            @PathVariable Long id,
            @RequestParam BarberHairstyle.Proficiency proficiency) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        BarberHairstyleResponse response = barberHairstyleService.updateProficiency(merchantId, id, proficiency);
        return ResponseEntity.ok(ApiResponse.success("更新成功", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除理发师发型", description = "删除理发师擅长的发型")
    public ResponseEntity<ApiResponse<Void>> deleteBarberHairstyle(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        barberHairstyleService.deleteBarberHairstyle(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}
