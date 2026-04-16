package com.barbershop.saas.service;

import com.barbershop.saas.common.BusinessException;
import com.barbershop.saas.common.MerchantContext;
import com.barbershop.saas.dto.HairstyleDTO;
import com.barbershop.saas.entity.Hairstyle;
import com.barbershop.saas.mapper.HairstyleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HairstyleService extends ServiceImpl<HairstyleMapper, Hairstyle> {

    private Long getCurrentMerchantId() {
        Long merchantId = MerchantContext.getMerchantId();
        if (merchantId == null) {
            throw new BusinessException("未获取到商家信息");
        }
        return merchantId;
    }

    public void addHairstyle(HairstyleDTO dto) {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<Hairstyle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hairstyle::getMerchantId, merchantId);
        wrapper.eq(Hairstyle::getName, dto.getName());
        Hairstyle exist = getOne(wrapper);
        if (exist != null) {
            throw new BusinessException("该发型名称已存在");
        }

        Hairstyle hairstyle = new Hairstyle();
        hairstyle.setMerchantId(merchantId);
        hairstyle.setName(dto.getName());
        hairstyle.setCategory(dto.getCategory());
        hairstyle.setImage(dto.getImage());
        hairstyle.setDescription(dto.getDescription());
        hairstyle.setPrice(dto.getPrice());
        hairstyle.setDuration(dto.getDuration());
        hairstyle.setSuitableGender(dto.getSuitableGender());
        hairstyle.setSuitableAge(dto.getSuitableAge());
        hairstyle.setDetailOptions(dto.getDetailOptions());
        hairstyle.setSkillTags(dto.getSkillTags());
        hairstyle.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        save(hairstyle);
    }

    public void updateHairstyle(Long id, HairstyleDTO dto) {
        Long merchantId = getCurrentMerchantId();
        Hairstyle hairstyle = getById(id);
        if (hairstyle == null || !hairstyle.getMerchantId().equals(merchantId)) {
            throw new BusinessException("发型不存在");
        }

        if (!hairstyle.getName().equals(dto.getName())) {
            LambdaQueryWrapper<Hairstyle> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Hairstyle::getMerchantId, merchantId);
            wrapper.eq(Hairstyle::getName, dto.getName());
            Hairstyle exist = getOne(wrapper);
            if (exist != null) {
                throw new BusinessException("该发型名称已存在");
            }
        }

        hairstyle.setName(dto.getName());
        hairstyle.setCategory(dto.getCategory());
        hairstyle.setImage(dto.getImage());
        hairstyle.setDescription(dto.getDescription());
        hairstyle.setPrice(dto.getPrice());
        hairstyle.setDuration(dto.getDuration());
        hairstyle.setSuitableGender(dto.getSuitableGender());
        hairstyle.setSuitableAge(dto.getSuitableAge());
        hairstyle.setDetailOptions(dto.getDetailOptions());
        hairstyle.setSkillTags(dto.getSkillTags());
        hairstyle.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        updateById(hairstyle);
    }

    public List<Hairstyle> list(String category, String keyword) {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<Hairstyle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hairstyle::getMerchantId, merchantId);
        wrapper.eq(Hairstyle::getStatus, 1);
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Hairstyle::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Hairstyle::getName, keyword);
        }
        wrapper.orderByDesc(Hairstyle::getCreateTime);
        return list(wrapper);
    }

    public List<String> getAllCategories() {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<Hairstyle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hairstyle::getMerchantId, merchantId);
        wrapper.eq(Hairstyle::getStatus, 1);
        wrapper.select(Hairstyle::getCategory);
        wrapper.groupBy(Hairstyle::getCategory);
        return list(wrapper).stream().map(Hairstyle::getCategory).collect(java.util.stream.Collectors.toList());
    }
}
