package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.Barber;
import com.saas.barbershop.entity.BarberHairstyle;
import com.saas.barbershop.entity.HairstyleType;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.BarberHairstyleRepository;
import com.saas.barbershop.repository.BarberRepository;
import com.saas.barbershop.repository.HairstyleTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BarberHairstyleService {

    @Autowired
    private BarberHairstyleRepository barberHairstyleRepository;

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private HairstyleTypeRepository hairstyleTypeRepository;

    @Transactional
    public BarberHairstyleResponse addBarberHairstyle(Long merchantId, BarberHairstyleRequest request) {
        Barber barber = barberRepository.findByIdAndMerchantId(request.getBarberId(), merchantId)
                .orElseThrow(() -> new BusinessException("理发师不存在"));

        HairstyleType hairstyleType = hairstyleTypeRepository.findByIdAndMerchantId(request.getHairstyleTypeId(), merchantId)
                .orElseThrow(() -> new BusinessException("发型类型不存在"));

        barberHairstyleRepository.findByBarberIdAndHairstyleTypeId(request.getBarberId(), request.getHairstyleTypeId())
                .ifPresent(bh -> {
                    throw new BusinessException("该理发师已添加此发型");
                });

        BarberHairstyle barberHairstyle = new BarberHairstyle();
        barberHairstyle.setBarber(barber);
        barberHairstyle.setHairstyleType(hairstyleType);
        barberHairstyle.setProficiency(BarberHairstyle.Proficiency.MEDIUM);

        BarberHairstyle saved = barberHairstyleRepository.save(barberHairstyle);
        return convertToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BarberHairstyleResponse> getBarberHairstyles(Long merchantId, Long barberId) {
        Barber barber = barberRepository.findByIdAndMerchantId(barberId, merchantId)
                .orElseThrow(() -> new BusinessException("理发师不存在"));

        return barberHairstyleRepository.findByBarberId(barberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BarberHairstyleResponse updateProficiency(Long merchantId, Long id, BarberHairstyle.Proficiency proficiency) {
        BarberHairstyle barberHairstyle = barberHairstyleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("记录不存在"));

        if (!barberHairstyle.getBarber().getMerchant().getId().equals(merchantId)) {
            throw new BusinessException("无权操作");
        }

        barberHairstyle.setProficiency(proficiency);
        BarberHairstyle updated = barberHairstyleRepository.save(barberHairstyle);
        return convertToResponse(updated);
    }

    @Transactional
    public void deleteBarberHairstyle(Long merchantId, Long id) {
        BarberHairstyle barberHairstyle = barberHairstyleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("记录不存在"));

        if (!barberHairstyle.getBarber().getMerchant().getId().equals(merchantId)) {
            throw new BusinessException("无权操作");
        }

        barberHairstyle.setIsDeleted(true);
        barberHairstyleRepository.save(barberHairstyle);
    }

    private BarberHairstyleResponse convertToResponse(BarberHairstyle barberHairstyle) {
        BarberHairstyleResponse response = new BarberHairstyleResponse();
        response.setId(barberHairstyle.getId());
        response.setBarberId(barberHairstyle.getBarber().getId());
        response.setBarberName(barberHairstyle.getBarber().getName());
        response.setHairstyleTypeId(barberHairstyle.getHairstyleType().getId());
        response.setHairstyleName(barberHairstyle.getHairstyleType().getName());
        response.setProficiency(barberHairstyle.getProficiency());
        response.setCreatedAt(barberHairstyle.getCreatedAt());
        return response;
    }
}
