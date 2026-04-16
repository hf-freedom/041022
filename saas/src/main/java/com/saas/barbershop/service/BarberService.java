package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.Barber;
import com.saas.barbershop.entity.BarberLevel;
import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.BarberLevelRepository;
import com.saas.barbershop.repository.BarberRepository;
import com.saas.barbershop.repository.MerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BarberService {

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private BarberLevelRepository barberLevelRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Transactional
    public BarberResponse createBarber(Long merchantId, BarberCreateRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在"));

        Barber barber = new Barber();
        barber.setMerchant(merchant);
        barber.setName(request.getName());
        barber.setPhone(request.getPhone());
        barber.setGender(request.getGender());
        barber.setBirthday(request.getBirthday());
        barber.setEntryDate(request.getEntryDate());
        barber.setSpecialties(request.getSpecialties());
        barber.setIntroduction(request.getIntroduction());

        if (request.getLevelId() != null) {
            BarberLevel level = barberLevelRepository.findByIdAndMerchantId(request.getLevelId(), merchantId)
                    .orElseThrow(() -> new BusinessException("理发师级别不存在"));
            barber.setBarberLevel(level);
        } else {
            BarberLevel defaultLevel = barberLevelRepository.findDefaultByMerchantId(merchantId)
                    .orElseThrow(() -> new BusinessException("未设置默认理发师级别"));
            barber.setBarberLevel(defaultLevel);
        }

        Barber savedBarber = barberRepository.save(barber);
        return convertToResponse(savedBarber);
    }

    @Transactional(readOnly = true)
    public BarberResponse getBarber(Long merchantId, Long barberId) {
        Barber barber = barberRepository.findByIdAndMerchantId(barberId, merchantId)
                .orElseThrow(() -> new BusinessException("理发师不存在"));
        return convertToResponse(barber);
    }

    @Transactional(readOnly = true)
    public List<BarberResponse> getAllBarbers(Long merchantId) {
        return barberRepository.findByMerchantId(merchantId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BarberResponse> getActiveBarbers(Long merchantId) {
        return barberRepository.findActiveByMerchantId(merchantId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<BarberResponse> getBarbers(Long merchantId, Pageable pageable) {
        return barberRepository.findByMerchantId(merchantId, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<BarberResponse> searchBarbers(Long merchantId, String keyword, Pageable pageable) {
        return barberRepository.findByMerchantIdAndKeyword(merchantId, keyword, pageable)
                .map(this::convertToResponse);
    }

    @Transactional
    public BarberResponse updateBarber(Long merchantId, Long barberId, BarberUpdateRequest request) {
        Barber barber = barberRepository.findByIdAndMerchantId(barberId, merchantId)
                .orElseThrow(() -> new BusinessException("理发师不存在"));

        barber.setName(request.getName());
        barber.setPhone(request.getPhone());
        barber.setGender(request.getGender());
        barber.setBirthday(request.getBirthday());
        barber.setSpecialties(request.getSpecialties());
        barber.setIntroduction(request.getIntroduction());
        barber.setIsActive(request.getIsActive());

        if (request.getLevelId() != null) {
            BarberLevel level = barberLevelRepository.findByIdAndMerchantId(request.getLevelId(), merchantId)
                    .orElseThrow(() -> new BusinessException("理发师级别不存在"));
            barber.setBarberLevel(level);
        }

        Barber updatedBarber = barberRepository.save(barber);
        return convertToResponse(updatedBarber);
    }

    @Transactional
    public void deleteBarber(Long merchantId, Long barberId) {
        Barber barber = barberRepository.findByIdAndMerchantId(barberId, merchantId)
                .orElseThrow(() -> new BusinessException("理发师不存在"));

        barber.setIsDeleted(true);
        barberRepository.save(barber);
    }

    private BarberResponse convertToResponse(Barber barber) {
        BarberResponse response = new BarberResponse();
        response.setId(barber.getId());
        response.setName(barber.getName());
        response.setPhone(barber.getPhone());
        response.setAvatarUrl(barber.getAvatarUrl());
        response.setGender(barber.getGender());
        response.setBirthday(barber.getBirthday());
        response.setEntryDate(barber.getEntryDate());

        if (barber.getBarberLevel() != null) {
            BarberLevelResponse levelResponse = new BarberLevelResponse();
            levelResponse.setId(barber.getBarberLevel().getId());
            levelResponse.setName(barber.getBarberLevel().getName());
            levelResponse.setLevel(barber.getBarberLevel().getLevel());
            levelResponse.setCommissionRate(barber.getBarberLevel().getCommissionRate());
            response.setBarberLevel(levelResponse);
        }

        response.setSpecialties(barber.getSpecialties());
        response.setIntroduction(barber.getIntroduction());
        response.setIsActive(barber.getIsActive());
        response.setOrderCount(barber.getOrderCount());
        response.setRating(barber.getRating());
        response.setCreatedAt(barber.getCreatedAt());
        return response;
    }
}
