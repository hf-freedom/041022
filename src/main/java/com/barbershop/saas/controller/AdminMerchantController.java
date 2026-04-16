package com.barbershop.saas.controller;

import com.barbershop.saas.common.Result;
import com.barbershop.saas.entity.Merchant;
import com.barbershop.saas.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/merchant")
public class AdminMerchantController {

    @Autowired
    private MerchantService merchantService;

    @GetMapping("/list")
    public Result<List<Merchant>> list(@RequestParam(required = false) Integer status) {
        List<Merchant> list = merchantService.listForAdmin(status);
        list.forEach(m -> m.setPassword(null));
        return Result.success(list);
    }

    @PostMapping("/audit")
    public Result<Void> audit(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString());
        String auditRemark = (String) params.get("auditRemark");
        merchantService.audit(id, status, auditRemark);
        return Result.success();
    }

    @PostMapping("/toggle-online")
    public Result<Void> toggleOnline(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer online = Integer.valueOf(params.get("online").toString());
        merchantService.toggleOnline(id, online);
        return Result.success();
    }
}
