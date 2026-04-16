package com.barbershop.saas.service;

import com.barbershop.saas.common.BusinessException;
import com.barbershop.saas.common.JwtUtil;
import com.barbershop.saas.dto.MerchantLoginDTO;
import com.barbershop.saas.dto.MerchantRegisterDTO;
import com.barbershop.saas.entity.Merchant;
import com.barbershop.saas.mapper.MerchantMapper;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MerchantService extends ServiceImpl<MerchantMapper, Merchant> {

    @Autowired
    private JwtUtil jwtUtil;

    public void register(MerchantRegisterDTO dto) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUsername, dto.getUsername());
        Merchant exist = getOne(wrapper);
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }

        wrapper.clear();
        wrapper.eq(Merchant::getPhone, dto.getPhone());
        exist = getOne(wrapper);
        if (exist != null) {
            throw new BusinessException("手机号已被注册");
        }

        Merchant merchant = new Merchant();
        merchant.setShopName(dto.getShopName());
        merchant.setContactName(dto.getContactName());
        merchant.setPhone(dto.getPhone());
        merchant.setEmail(dto.getEmail());
        merchant.setAddress(dto.getAddress());
        merchant.setUsername(dto.getUsername());
        merchant.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()));
        merchant.setBusinessLicense(dto.getBusinessLicense());
        merchant.setIdCardFront(dto.getIdCardFront());
        merchant.setIdCardBack(dto.getIdCardBack());
        merchant.setStatus(0);
        merchant.setOnline(0);
        save(merchant);
    }

    public Map<String, Object> login(MerchantLoginDTO dto) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getUsername, dto.getUsername());
        Merchant merchant = getOne(wrapper);
        if (merchant == null) {
            throw new BusinessException("用户名或密码错误");
        }

        String md5Password = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
        if (!md5Password.equals(merchant.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (merchant.getStatus() == 0) {
            throw new BusinessException("商家审核中，请等待");
        }
        if (merchant.getStatus() == 2) {
            throw new BusinessException("商家审核未通过：" + merchant.getAuditRemark());
        }
        if (merchant.getOnline() == 0) {
            throw new BusinessException("商家已下线，请联系管理员");
        }

        String token = jwtUtil.generateToken(merchant.getId(), merchant.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("merchantId", merchant.getId());
        result.put("shopName", merchant.getShopName());
        return result;
    }

    public void audit(Long id, Integer status, String auditRemark) {
        Merchant merchant = getById(id);
        if (merchant == null) {
            throw new BusinessException("商家不存在");
        }
        merchant.setStatus(status);
        merchant.setAuditRemark(auditRemark);
        if (status == 1) {
            merchant.setOnline(1);
        }
        updateById(merchant);
    }

    public void toggleOnline(Long id, Integer online) {
        Merchant merchant = getById(id);
        if (merchant == null) {
            throw new BusinessException("商家不存在");
        }
        merchant.setOnline(online);
        updateById(merchant);
    }

    public List<Merchant> listForAdmin(Integer status) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Merchant::getStatus, status);
        }
        wrapper.orderByDesc(Merchant::getCreateTime);
        return list(wrapper);
    }
}
