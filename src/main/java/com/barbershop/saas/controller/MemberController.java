package com.barbershop.saas.controller;

import com.barbershop.saas.common.Result;
import com.barbershop.saas.dto.MemberDTO;
import com.barbershop.saas.dto.RechargeDTO;
import com.barbershop.saas.entity.Member;
import com.barbershop.saas.entity.MemberLevel;
import com.barbershop.saas.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/add")
    public Result<Void> addMember(@Valid @RequestBody MemberDTO dto) {
        memberService.addMember(dto);
        return Result.success();
    }

    @PostMapping("/bind-phone")
    public Result<Void> bindPhone(@RequestBody Map<String, Object> params) {
        Long memberId = Long.valueOf(params.get("memberId").toString());
        String phone = (String) params.get("phone");
        memberService.bindPhone(memberId, phone);
        return Result.success();
    }

    @PostMapping("/recharge")
    public Result<Void> recharge(@Valid @RequestBody RechargeDTO dto) {
        memberService.recharge(dto);
        return Result.success();
    }

    @PostMapping("/consume")
    public Result<Void> consume(@RequestBody Map<String, Object> params) {
        Long memberId = Long.valueOf(params.get("memberId").toString());
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        memberService.consume(memberId, amount);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Member>> list(@RequestParam(required = false) String keyword) {
        List<Member> list = memberService.list(keyword);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Member> getById(@PathVariable Long id) {
        Member member = memberService.getById(id);
        return Result.success(member);
    }

    @GetMapping("/levels")
    public Result<List<MemberLevel>> listLevels() {
        List<MemberLevel> list = memberService.listLevels();
        return Result.success(list);
    }
}
