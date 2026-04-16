package com.barbershop.saas.service;

import com.barbershop.saas.common.BusinessException;
import com.barbershop.saas.common.MerchantContext;
import com.barbershop.saas.dto.BarberDTO;
import com.barbershop.saas.entity.Barber;
import com.barbershop.saas.entity.BarberLevel;
import com.barbershop.saas.mapper.BarberLevelMapper;
import com.barbershop.saas.mapper.BarberMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BarberService extends ServiceImpl<BarberMapper, Barber> {

    @Autowired
    private BarberLevelMapper barberLevelMapper;

    private Long getCurrentMerchantId() {
        Long merchantId = MerchantContext.getMerchantId();
        if (merchantId == null) {
            throw new BusinessException("未获取到商家信息");
        }
        return merchantId;
    }

    public void addBarber(BarberDTO dto) {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<Barber> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Barber::getMerchantId, merchantId);
        wrapper.eq(Barber::getPhone, dto.getPhone());
        Barber exist = getOne(wrapper);
        if (exist != null) {
            throw new BusinessException("该手机号已注册理发师");
        }

        BarberLevel level = getBarberLevel(merchantId, dto.getLevelId());

        Barber barber = new Barber();
        barber.setMerchantId(merchantId);
        barber.setName(dto.getName());
        barber.setPhone(dto.getPhone());
        barber.setAvatar(dto.getAvatar());
        barber.setLevelId(dto.getLevelId());
        barber.setLevelName(level.getName());
        barber.setCommissionRate(level.getCommissionRate());
        barber.setSkillTags(dto.getSkillTags());
        barber.setOrderCount(0);
        barber.setTotalCommission(BigDecimal.ZERO);
        barber.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        save(barber);
    }

    public void updateBarber(Long id, BarberDTO dto) {
        Long merchantId = getCurrentMerchantId();
        Barber barber = getById(id);
        if (barber == null || !barber.getMerchantId().equals(merchantId)) {
            throw new BusinessException("理发师不存在");
        }

        if (!barber.getPhone().equals(dto.getPhone())) {
            LambdaQueryWrapper<Barber> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Barber::getMerchantId, merchantId);
            wrapper.eq(Barber::getPhone, dto.getPhone());
            Barber exist = getOne(wrapper);
            if (exist != null) {
                throw new BusinessException("该手机号已注册理发师");
            }
        }

        BarberLevel level = getBarberLevel(merchantId, dto.getLevelId());

        barber.setName(dto.getName());
        barber.setPhone(dto.getPhone());
        barber.setAvatar(dto.getAvatar());
        barber.setLevelId(dto.getLevelId());
        barber.setLevelName(level.getName());
        barber.setCommissionRate(level.getCommissionRate());
        barber.setSkillTags(dto.getSkillTags());
        barber.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        updateById(barber);
    }

    private BarberLevel getBarberLevel(Long merchantId, Integer levelId) {
        LambdaQueryWrapper<BarberLevel> levelWrapper = new LambdaQueryWrapper<>();
        levelWrapper.eq(BarberLevel::getMerchantId, merchantId);
        List<BarberLevel> levels = barberLevelMapper.selectList(levelWrapper);
        if (levels.isEmpty()) {
            initDefaultLevels(merchantId);
            levels = barberLevelMapper.selectList(levelWrapper);
        }

        BarberLevel level = levels.stream()
                .filter(l -> l.getId().intValue() == levelId)
                .findFirst()
                .orElse(null);
        if (level == null) {
            throw new BusinessException("级别不存在");
        }
        return level;
    }

    private void initDefaultLevels(Long merchantId) {
        String[] levelNames = {"助理", "发型师", "高级发型师", "技术总监"};
        BigDecimal[] rates = {new BigDecimal("0.10"), new BigDecimal("0.20"), new BigDecimal("0.30"), new BigDecimal("0.40")};

        for (int i = 0; i < levelNames.length; i++) {
            BarberLevel level = new BarberLevel();
            level.setMerchantId(merchantId);
            level.setName(levelNames[i]);
            level.setCommissionRate(rates[i]);
            barberLevelMapper.insert(level);
        }
    }

    public List<Barber> matchBarbers(Long hairstyleId, List<String> detailOptions, String skillTags) {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<Barber> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Barber::getMerchantId, merchantId);
        wrapper.eq(Barber::getStatus, 1);

        List<Barber> allBarbers = list(wrapper);
        List<String> requiredSkills = detailOptions != null ? detailOptions.stream()
                .flatMap(opt -> Arrays.stream(opt.split(",")))
                .collect(Collectors.toList()) : new java.util.ArrayList<>();

        if (skillTags != null && !skillTags.isEmpty()) {
            requiredSkills.addAll(Arrays.asList(skillTags.split(",")));
        }

        return allBarbers.stream()
                .sorted((b1, b2) -> {
                    int score1 = calculateMatchScore(b1, requiredSkills);
                    int score2 = calculateMatchScore(b2, requiredSkills);
                    return Integer.compare(score2, score1);
                })
                .collect(Collectors.toList());
    }

    private int calculateMatchScore(Barber barber, List<String> requiredSkills) {
        if (barber.getSkillTags() == null || barber.getSkillTags().isEmpty()) {
            return 0;
        }
        List<String> barberSkills = Arrays.asList(barber.getSkillTags().split(","));
        return (int) requiredSkills.stream()
                .filter(barberSkills::contains)
                .count();
    }

    public List<Barber> list(String keyword) {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<Barber> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Barber::getMerchantId, merchantId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Barber::getName, keyword).or().like(Barber::getPhone, keyword));
        }
        wrapper.orderByDesc(Barber::getCreateTime);
        return list(wrapper);
    }

    public List<BarberLevel> listLevels() {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<BarberLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BarberLevel::getMerchantId, merchantId);
        return barberLevelMapper.selectList(wrapper);
    }
}
