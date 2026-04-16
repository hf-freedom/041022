package com.saas.barbershop.service;

import com.saas.barbershop.dto.MemberLevelRequest;
import com.saas.barbershop.dto.MemberLevelResponse;
import com.saas.barbershop.entity.MemberLevel;
import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.MemberLevelRepository;
import com.saas.barbershop.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MemberLevelService {

    @Autowired
    private MemberLevelRepository memberLevelRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Transactional
    public MemberLevelResponse createLevel(Long merchantId, MemberLevelRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在"));

        MemberLevel level = new MemberLevel();
        level.setMerchant(merchant);
        level.setName(request.getName());
        level.setLevel(request.getLevel());
        level.setMinAmount(request.getMinAmount());
        level.setDiscountRate(request.getDiscountRate());
        level.setDescription(request.getDescription());
        level.setIsDefault(request.getIsDefault());

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            memberLevelRepository.findDefaultByMerchantId(merchantId)
                    .ifPresent(defaultLevel -> {
                        defaultLevel.setIsDefault(false);
                        memberLevelRepository.save(defaultLevel);
                    });
        }

        MemberLevel savedLevel = memberLevelRepository.save(level);
        return convertToResponse(savedLevel);
    }

    @Transactional(readOnly = true)
    public List<MemberLevelResponse> getAllLevels(Long merchantId) {
        return memberLevelRepository.findByMerchantIdOrderByLevelAsc(merchantId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberLevelResponse getLevel(Long merchantId, Long levelId) {
        MemberLevel level = memberLevelRepository.findByIdAndMerchantId(levelId, merchantId)
                .orElseThrow(() -> new BusinessException("会员等级不存在"));
        return convertToResponse(level);
    }

    @Transactional
    public MemberLevelResponse updateLevel(Long merchantId, Long levelId, MemberLevelRequest request) {
        MemberLevel level = memberLevelRepository.findByIdAndMerchantId(levelId, merchantId)
                .orElseThrow(() -> new BusinessException("会员等级不存在"));

        level.setName(request.getName());
        level.setLevel(request.getLevel());
        level.setMinAmount(request.getMinAmount());
        level.setDiscountRate(request.getDiscountRate());
        level.setDescription(request.getDescription());

        if (Boolean.TRUE.equals(request.getIsDefault()) && !level.getIsDefault()) {
            memberLevelRepository.findDefaultByMerchantId(merchantId)
                    .ifPresent(defaultLevel -> {
                        defaultLevel.setIsDefault(false);
                        memberLevelRepository.save(defaultLevel);
                    });
            level.setIsDefault(true);
        }

        MemberLevel updatedLevel = memberLevelRepository.save(level);
        return convertToResponse(updatedLevel);
    }

    @Transactional
    public void deleteLevel(Long merchantId, Long levelId) {
        MemberLevel level = memberLevelRepository.findByIdAndMerchantId(levelId, merchantId)
                .orElseThrow(() -> new BusinessException("会员等级不存在"));

        if (Boolean.TRUE.equals(level.getIsDefault())) {
            throw new BusinessException("默认等级不能删除");
        }

        level.setIsDeleted(true);
        memberLevelRepository.save(level);
    }

    private MemberLevelResponse convertToResponse(MemberLevel level) {
        MemberLevelResponse response = new MemberLevelResponse();
        response.setId(level.getId());
        response.setName(level.getName());
        response.setLevel(level.getLevel());
        response.setDiscountRate(level.getDiscountRate());
        return response;
    }
}
