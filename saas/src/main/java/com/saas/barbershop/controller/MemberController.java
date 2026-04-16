package com.saas.barbershop.controller;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.service.MemberService;
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
@RequestMapping("/members")
@Tag(name = "会员管理", description = "会员CRUD、充值、消费等接口")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping
    @Operation(summary = "创建会员", description = "录入新会员信息")
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(@Valid @RequestBody MemberCreateRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        MemberResponse response = memberService.createMember(merchantId, request);
        return ResponseEntity.ok(ApiResponse.success("会员创建成功", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取会员详情", description = "根据ID获取会员详细信息")
    public ResponseEntity<ApiResponse<MemberResponse>> getMember(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        MemberResponse response = memberService.getMember(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "根据手机号获取会员", description = "根据手机号查询会员")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberByPhone(@PathVariable String phone) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        MemberResponse response = memberService.getMemberByPhone(merchantId, phone);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "获取会员列表", description = "分页获取会员列表")
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> getMembers(Pageable pageable) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        Page<MemberResponse> response = memberService.getMembers(merchantId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有会员", description = "获取所有会员列表")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMembers() {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<MemberResponse> response = memberService.getAllMembers(merchantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索会员", description = "根据姓名或手机号搜索会员")
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> searchMembers(
            @RequestParam String keyword,
            Pageable pageable) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        Page<MemberResponse> response = memberService.searchMembers(merchantId, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新会员", description = "更新会员信息")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberUpdateRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        MemberResponse response = memberService.updateMember(merchantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("会员信息更新成功", response));
    }

    @PostMapping("/{id}/recharge")
    @Operation(summary = "会员充值", description = "为会员账户充值")
    public ResponseEntity<ApiResponse<MemberResponse>> recharge(
            @PathVariable Long id,
            @Valid @RequestBody RechargeRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        MemberResponse response = memberService.recharge(merchantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("充值成功", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除会员", description = "删除会员（逻辑删除）")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        memberService.deleteMember(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success("会员已删除", null));
    }
}
