package com.saas.barbershop.service;

import com.saas.barbershop.dto.BarberLevelRequest;
import com.saas.barbershop.dto.BarberLevelResponse;
import com.saas.barbershop.entity.BarberLevel;
import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.BarberLevelRepository;
import com.saas.barbershop.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BarberLevelService {

    @Autowired
    private BarberLevelRepository barberLevelRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Transactional
    public BarberLevelResponse createLevel(Long merchantId, BarberLevelRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在"));

        BarberLevel level = new BarberLevel();
        level.setMerchant(merchant);
        level.setName(request.getName());
        level.setLevel(request.getLevel());
        level.setCommissionRate(request.getCommissionRate());
        level.setDescription(request.getDescription());
        level.setIsDefault(request.getIsDefault());

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            barberLevelRepository.findDefaultByMerchantId(merchantId)
                    .ifPresent(defaultLevel -> {
                        defaultLevel.setIsDefault(false);
                        barberLevelRepository.save(defaultLevel);
                    });
        }

        BarberLevel savedLevel = barberLevelRepository.save(level);
        return convertToResponse(savedLevel);
    }

    @Transactional(readOnly = true)
    public List<BarberLevelResponse> getAllLevels(Long merchantId) {
        return barberLevelRepository.findByMerchantIdOrderByLevelAsc(merchantId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BarberLevelResponse getLevel(Long merchantId, Long levelId) {
        BarberLevel level = barberLevelRepository.findByIdAndMerchantId(levelId, merchantId)
                .orElseThrow(() -> new BusinessException("理发师级别不存在"));
        return convertToResponse(level);
    }

    @Transactional
    public BarberLevelResponse updateLevel(Long merchantId, Long levelId, BarberLevelRequest request) {
        BarberLevel level = barberLevelRepository.findByIdAndMerchantId(levelId, merchantId)
                .orElseThrow(() -> new BusinessException("理发师级别不存在"));

        level.setName(request.getName());
        level.setLevel(request.getLevel());
        level.setCommissionRate(request.getCommissionRate());
        level.setDescription(request.getDescription());

        if (Boolean.TRUE.equals(request.getIsDefault()) && !level.getIsDefault()) {
            barberLevelRepository.findDefaultByMerchantId(merchantId)
                    .ifPresent(defaultLevel -> {
                        defaultLevel.setIsDefault(false);
                        barberLevelRepository.save(defaultLevel);
                    });
            level.setIsDefault(true);
        }

        BarberLevel updatedLevel = barberLevelRepository.save(level);
        return convertToResponse(updatedLevel);
    }

    @Transactional
    public void deleteLevel(Long merchantId, Long levelId) {
        BarberLevel level = barberLevelRepository.findByIdAndMerchantId(levelId, merchantId)
                .orElseThrow(() -> new BusinessException("理发师级别不存在"));

        if (Boolean.TRUE.equals(level.getIsDefault())) {
            throw new BusinessException("默认级别不能删除");
        }

        level.setIsDeleted(true);
        barberLevelRepository.save(level);
    }

    private BarberLevelResponse convertToResponse(BarberLevel level) {
        BarberLevelResponse response = new BarberLevelResponse();
        response.setId(level.getId());
        response.setName(level.getName());
        response.setLevel(level.getLevel());
        response.setCommissionRate(level.getCommissionRate());
        return response;
    }
}
