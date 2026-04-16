package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.HairstyleDetail;
import com.saas.barbershop.entity.HairstyleType;
import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.HairstyleDetailRepository;
import com.saas.barbershop.repository.HairstyleTypeRepository;
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
public class HairstyleService {

    @Autowired
    private HairstyleTypeRepository hairstyleTypeRepository;

    @Autowired
    private HairstyleDetailRepository hairstyleDetailRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Transactional
    public HairstyleTypeResponse createHairstyleType(Long merchantId, HairstyleTypeRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在"));

        HairstyleType type = new HairstyleType();
        type.setMerchant(merchant);
        type.setName(request.getName());
        type.setCategory(request.getCategory());
        type.setBasePrice(request.getBasePrice());
        type.setDurationMinutes(request.getDurationMinutes());
        type.setImageUrl(request.getImageUrl());
        type.setDescription(request.getDescription());

        HairstyleType savedType = hairstyleTypeRepository.save(type);
        return convertTypeToResponse(savedType);
    }

    @Transactional(readOnly = true)
    public HairstyleTypeResponse getHairstyleType(Long merchantId, Long typeId) {
        HairstyleType type = hairstyleTypeRepository.findByIdAndMerchantId(typeId, merchantId)
                .orElseThrow(() -> new BusinessException("发型类型不存在"));
        return convertTypeToResponse(type);
    }

    @Transactional(readOnly = true)
    public List<HairstyleTypeResponse> getAllHairstyleTypes(Long merchantId) {
        return hairstyleTypeRepository.findByMerchantId(merchantId).stream()
                .map(this::convertTypeToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HairstyleTypeResponse> getActiveHairstyleTypes(Long merchantId) {
        return hairstyleTypeRepository.findActiveByMerchantId(merchantId).stream()
                .map(this::convertTypeToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HairstyleTypeResponse> getHairstyleTypesByCategory(Long merchantId, HairstyleType.Category category) {
        return hairstyleTypeRepository.findByMerchantIdAndCategory(merchantId, category).stream()
                .map(this::convertTypeToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<HairstyleTypeResponse> getHairstyleTypes(Long merchantId, Pageable pageable) {
        return hairstyleTypeRepository.findByMerchantId(merchantId, pageable)
                .map(this::convertTypeToResponse);
    }

    @Transactional
    public HairstyleTypeResponse updateHairstyleType(Long merchantId, Long typeId, HairstyleTypeRequest request) {
        HairstyleType type = hairstyleTypeRepository.findByIdAndMerchantId(typeId, merchantId)
                .orElseThrow(() -> new BusinessException("发型类型不存在"));

        type.setName(request.getName());
        type.setCategory(request.getCategory());
        type.setBasePrice(request.getBasePrice());
        type.setDurationMinutes(request.getDurationMinutes());
        type.setImageUrl(request.getImageUrl());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive());

        HairstyleType updatedType = hairstyleTypeRepository.save(type);
        return convertTypeToResponse(updatedType);
    }

    @Transactional
    public void deleteHairstyleType(Long merchantId, Long typeId) {
        HairstyleType type = hairstyleTypeRepository.findByIdAndMerchantId(typeId, merchantId)
                .orElseThrow(() -> new BusinessException("发型类型不存在"));

        type.setIsDeleted(true);
        hairstyleTypeRepository.save(type);
    }

    @Transactional
    public HairstyleDetailResponse createHairstyleDetail(Long merchantId, Long typeId, HairstyleDetailRequest request) {
        HairstyleType type = hairstyleTypeRepository.findByIdAndMerchantId(typeId, merchantId)
                .orElseThrow(() -> new BusinessException("发型类型不存在"));

        HairstyleDetail detail = new HairstyleDetail();
        detail.setHairstyleType(type);
        detail.setName(request.getName());
        detail.setAdditionalPrice(request.getAdditionalPrice());
        detail.setAdditionalMinutes(request.getAdditionalMinutes());
        detail.setDescription(request.getDescription());

        HairstyleDetail savedDetail = hairstyleDetailRepository.save(detail);
        return convertDetailToResponse(savedDetail);
    }

    @Transactional(readOnly = true)
    public List<HairstyleDetailResponse> getHairstyleDetails(Long merchantId, Long typeId) {
        HairstyleType type = hairstyleTypeRepository.findByIdAndMerchantId(typeId, merchantId)
                .orElseThrow(() -> new BusinessException("发型类型不存在"));

        return hairstyleDetailRepository.findByHairstyleTypeId(typeId).stream()
                .map(this::convertDetailToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HairstyleDetailResponse> getActiveHairstyleDetails(Long merchantId, Long typeId) {
        HairstyleType type = hairstyleTypeRepository.findByIdAndMerchantId(typeId, merchantId)
                .orElseThrow(() -> new BusinessException("发型类型不存在"));

        return hairstyleDetailRepository.findActiveByHairstyleTypeId(typeId).stream()
                .map(this::convertDetailToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public HairstyleDetailResponse updateHairstyleDetail(Long merchantId, Long typeId, Long detailId, HairstyleDetailRequest request) {
        HairstyleType type = hairstyleTypeRepository.findByIdAndMerchantId(typeId, merchantId)
                .orElseThrow(() -> new BusinessException("发型类型不存在"));

        HairstyleDetail detail = hairstyleDetailRepository.findByIdAndHairstyleTypeId(detailId, typeId)
                .orElseThrow(() -> new BusinessException("发型细节不存在"));

        detail.setName(request.getName());
        detail.setAdditionalPrice(request.getAdditionalPrice());
        detail.setAdditionalMinutes(request.getAdditionalMinutes());
        detail.setDescription(request.getDescription());
        detail.setIsActive(request.getIsActive());

        HairstyleDetail updatedDetail = hairstyleDetailRepository.save(detail);
        return convertDetailToResponse(updatedDetail);
    }

    @Transactional
    public void deleteHairstyleDetail(Long merchantId, Long typeId, Long detailId) {
        HairstyleType type = hairstyleTypeRepository.findByIdAndMerchantId(typeId, merchantId)
                .orElseThrow(() -> new BusinessException("发型类型不存在"));

        HairstyleDetail detail = hairstyleDetailRepository.findByIdAndHairstyleTypeId(detailId, typeId)
                .orElseThrow(() -> new BusinessException("发型细节不存在"));

        detail.setIsDeleted(true);
        hairstyleDetailRepository.save(detail);
    }

    private HairstyleTypeResponse convertTypeToResponse(HairstyleType type) {
        HairstyleTypeResponse response = new HairstyleTypeResponse();
        response.setId(type.getId());
        response.setName(type.getName());
        response.setCategory(type.getCategory());
        response.setBasePrice(type.getBasePrice());
        response.setDurationMinutes(type.getDurationMinutes());
        response.setImageUrl(type.getImageUrl());
        response.setDescription(type.getDescription());
        response.setIsActive(type.getIsActive());
        response.setCreatedAt(type.getCreatedAt());
        return response;
    }

    private HairstyleDetailResponse convertDetailToResponse(HairstyleDetail detail) {
        HairstyleDetailResponse response = new HairstyleDetailResponse();
        response.setId(detail.getId());
        response.setHairstyleTypeId(detail.getHairstyleType().getId());
        response.setName(detail.getName());
        response.setAdditionalPrice(detail.getAdditionalPrice());
        response.setAdditionalMinutes(detail.getAdditionalMinutes());
        response.setDescription(detail.getDescription());
        response.setIsActive(detail.getIsActive());
        response.setCreatedAt(detail.getCreatedAt());
        return response;
    }
}
