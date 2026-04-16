package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.MerchantRepository;
import com.saas.barbershop.security.JwtTokenProvider;
import com.saas.barbershop.security.MerchantPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public MerchantResponse register(MerchantRegisterRequest request) {
        if (merchantRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("该手机号已被注册");
        }

        Merchant merchant = new Merchant();
        merchant.setShopName(request.getShopName());
        merchant.setOwnerName(request.getOwnerName());
        merchant.setPhone(request.getPhone());
        merchant.setPassword(passwordEncoder.encode(request.getPassword()));
        merchant.setEmail(request.getEmail());
        merchant.setAddress(request.getAddress());
        merchant.setBusinessLicense(request.getBusinessLicense());
        merchant.setDescription(request.getDescription());
        merchant.setStatus(Merchant.MerchantStatus.PENDING);

        Merchant savedMerchant = merchantRepository.save(merchant);
        return convertToResponse(savedMerchant);
    }

    @Transactional
    public LoginResponse login(MerchantLoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getPhone(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            MerchantPrincipal merchantPrincipal = (MerchantPrincipal) authentication.getPrincipal();
            Merchant merchant = merchantRepository.findById(merchantPrincipal.getId())
                    .orElseThrow(() -> new BusinessException("商家不存在"));

            if (merchant.getStatus() != Merchant.MerchantStatus.APPROVED) {
                throw new BusinessException(HttpStatus.FORBIDDEN, "账号未通过审核或已被禁用");
            }

            merchant.setLastLoginAt(LocalDateTime.now());
            merchantRepository.save(merchant);

            String jwt = tokenProvider.generateToken(authentication);

            return new LoginResponse(
                    jwt,
                    "Bearer",
                    86400L,
                    convertToResponse(merchant)
            );
        } catch (BadCredentialsException e) {
            throw new BusinessException("手机号或密码错误");
        }
    }

    @Transactional(readOnly = true)
    public MerchantResponse getCurrentMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findByIdAndIsDeletedFalse(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在"));
        return convertToResponse(merchant);
    }

    @Transactional
    public MerchantResponse updateMerchant(Long merchantId, MerchantRegisterRequest request) {
        Merchant merchant = merchantRepository.findByIdAndIsDeletedFalse(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在"));

        if (!merchant.getPhone().equals(request.getPhone()) && merchantRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("该手机号已被其他账号使用");
        }

        merchant.setShopName(request.getShopName());
        merchant.setOwnerName(request.getOwnerName());
        merchant.setPhone(request.getPhone());
        merchant.setEmail(request.getEmail());
        merchant.setAddress(request.getAddress());
        merchant.setDescription(request.getDescription());

        Merchant updatedMerchant = merchantRepository.save(merchant);
        return convertToResponse(updatedMerchant);
    }

    @Transactional(readOnly = true)
    public Page<MerchantResponse> getAllMerchants(Pageable pageable) {
        return merchantRepository.findAllActive(pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<MerchantResponse> getMerchantsByStatus(Merchant.MerchantStatus status, Pageable pageable) {
        return merchantRepository.findByStatus(status, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<MerchantResponse> searchMerchants(String keyword, Pageable pageable) {
        return merchantRepository.findByKeyword(keyword, pageable)
                .map(this::convertToResponse);
    }

    @Transactional
    public MerchantResponse auditMerchant(Long merchantId, MerchantAuditRequest request) {
        Merchant merchant = merchantRepository.findByIdAndIsDeletedFalse(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在"));

        if (merchant.getStatus() != Merchant.MerchantStatus.PENDING) {
            throw new BusinessException("该商家已审核过");
        }

        merchant.setStatus(request.getStatus());
        merchant.setAuditRemark(request.getAuditRemark());
        merchant.setAuditedAt(LocalDateTime.now());

        Merchant updatedMerchant = merchantRepository.save(merchant);
        return convertToResponse(updatedMerchant);
    }

    @Transactional
    public void disableMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findByIdAndIsDeletedFalse(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在"));

        merchant.setStatus(Merchant.MerchantStatus.DISABLED);
        merchantRepository.save(merchant);
    }

    @Transactional
    public void enableMerchant(Long merchantId) {
        Merchant merchant = merchantRepository.findByIdAndIsDeletedFalse(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在"));

        merchant.setStatus(Merchant.MerchantStatus.APPROVED);
        merchantRepository.save(merchant);
    }

    private MerchantResponse convertToResponse(Merchant merchant) {
        MerchantResponse response = new MerchantResponse();
        response.setId(merchant.getId());
        response.setShopName(merchant.getShopName());
        response.setOwnerName(merchant.getOwnerName());
        response.setPhone(merchant.getPhone());
        response.setEmail(merchant.getEmail());
        response.setAddress(merchant.getAddress());
        response.setBusinessLicense(merchant.getBusinessLicense());
        response.setLogoUrl(merchant.getLogoUrl());
        response.setDescription(merchant.getDescription());
        response.setStatus(merchant.getStatus());
        response.setAuditRemark(merchant.getAuditRemark());
        response.setAuditedAt(merchant.getAuditedAt());
        response.setLastLoginAt(merchant.getLastLoginAt());
        response.setCreatedAt(merchant.getCreatedAt());
        return response;
    }
}
