package com.saas.barbershop.controller;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.service.MatchingService;
import com.saas.barbershop.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/matching")
@Tag(name = "智能匹配", description = "发型与理发师智能匹配接口")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @PostMapping("/barbers")
    @Operation(summary = "匹配理发师", description = "根据选择的发型和细节匹配适合的理发师")
    public ResponseEntity<ApiResponse<List<BarberMatchResponse>>> matchBarbers(
            @Valid @RequestBody BarberMatchRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<BarberMatchResponse> response = matchingService.matchBarbers(merchantId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/price")
    @Operation(summary = "计算价格", description = "根据选择的发型和细节计算价格")
    public ResponseEntity<ApiResponse<HairstylePriceResponse>> calculatePrice(
            @Valid @RequestBody HairstylePriceRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        HairstylePriceResponse response = matchingService.calculatePrice(merchantId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
