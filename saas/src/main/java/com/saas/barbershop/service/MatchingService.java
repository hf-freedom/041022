package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.*;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MatchingService {

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private HairstyleTypeRepository hairstyleTypeRepository;

    @Autowired
    private HairstyleDetailRepository hairstyleDetailRepository;

    @Autowired
    private BarberHairstyleRepository barberHairstyleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<BarberMatchResponse> matchBarbers(Long merchantId, BarberMatchRequest request) {
        HairstyleType hairstyleType = hairstyleTypeRepository.findByIdAndMerchantId(request.getHairstyleTypeId(), merchantId)
                .orElseThrow(() -> new BusinessException("发型类型不存在"));

        List<HairstyleDetail> selectedDetails = new ArrayList<>();
        if (request.getHairstyleDetailIds() != null && !request.getHairstyleDetailIds().isEmpty()) {
            for (Long detailId : request.getHairstyleDetailIds()) {
                HairstyleDetail detail = hairstyleDetailRepository.findByIdAndHairstyleTypeId(detailId, request.getHairstyleTypeId())
                        .orElseThrow(() -> new BusinessException("发型细节不存在: " + detailId));
                selectedDetails.add(detail);
            }
        }

        List<BarberHairstyle> barberHairstyles = barberHairstyleRepository.findByMerchantIdAndHairstyleTypeId(merchantId, request.getHairstyleTypeId());

        if (barberHairstyles.isEmpty()) {
            List<Barber> allBarbers = barberRepository.findActiveByMerchantId(merchantId);
            barberHairstyles = allBarbers.stream()
                    .map(barber -> {
                        BarberHairstyle bh = new BarberHairstyle();
                        bh.setBarber(barber);
                        bh.setHairstyleType(hairstyleType);
                        bh.setProficiency(BarberHairstyle.Proficiency.MEDIUM);
                        return bh;
                    })
                    .collect(Collectors.toList());
        }

        BigDecimal basePrice = hairstyleType.getBasePrice();
        BigDecimal additionalPrice = selectedDetails.stream()
                .map(HairstyleDetail::getAdditionalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPrice = basePrice.add(additionalPrice);

        int baseDuration = hairstyleType.getDurationMinutes();
        int additionalDuration = selectedDetails.stream()
                .mapToInt(HairstyleDetail::getAdditionalMinutes)
                .sum();
        int totalDuration = baseDuration + additionalDuration;

        Member member = null;
        BigDecimal discountRate = BigDecimal.ONE;
        if (request.getMemberId() != null) {
            member = memberRepository.findByIdAndMerchantId(request.getMemberId(), merchantId)
                    .orElse(null);
            if (member != null && member.getMemberLevel() != null) {
                discountRate = member.getMemberLevel().getDiscountRate();
            }
        }
        BigDecimal finalPrice = totalPrice.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);

        List<BarberMatchResponse> matches = new ArrayList<>();
        for (BarberHairstyle barberHairstyle : barberHairstyles) {
            Barber barber = barberHairstyle.getBarber();

            if (!barber.getIsActive()) {
                continue;
            }

            double score = calculateMatchScore(barber, barberHairstyle, request);

            BarberMatchResponse match = new BarberMatchResponse();
            match.setBarberId(barber.getId());
            match.setBarberName(barber.getName());
            match.setAvatarUrl(barber.getAvatarUrl());
            match.setBarberLevel(barber.getBarberLevel() != null ? barber.getBarberLevel().getName() : "未知");
            match.setRating(barber.getRating());
            match.setOrderCount(barber.getOrderCount());
            match.setProficiency(barberHairstyle.getProficiency());
            match.setSpecialties(barber.getSpecialties());
            match.setBasePrice(basePrice);
            match.setAdditionalPrice(additionalPrice);
            match.setTotalPrice(totalPrice);
            match.setDiscountRate(discountRate);
            match.setFinalPrice(finalPrice);
            match.setEstimatedDuration(totalDuration);
            match.setMatchScore(score);
            match.setRecommended(score >= 80);

            matches.add(match);
        }

        matches.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));

        return matches;
    }

    private double calculateMatchScore(Barber barber, BarberHairstyle barberHairstyle, BarberMatchRequest request) {
        double score = 0;

        switch (barberHairstyle.getProficiency()) {
            case EXPERT:
                score += 40;
                break;
            case ADVANCED:
                score += 30;
                break;
            case MEDIUM:
                score += 20;
                break;
            case BEGINNER:
                score += 10;
                break;
        }

        double ratingScore = barber.getRating().doubleValue() * 6;
        score += Math.min(ratingScore, 30);

        int orderCount = barber.getOrderCount();
        if (orderCount > 1000) {
            score += 20;
        } else if (orderCount > 500) {
            score += 15;
        } else if (orderCount > 100) {
            score += 10;
        } else if (orderCount > 10) {
            score += 5;
        }

        if (request.getPreferredGender() != null && barber.getGender() == request.getPreferredGender()) {
            score += 10;
        }

        return Math.min(score, 100);
    }

    @Transactional(readOnly = true)
    public HairstylePriceResponse calculatePrice(Long merchantId, HairstylePriceRequest request) {
        HairstyleType hairstyleType = hairstyleTypeRepository.findByIdAndMerchantId(request.getHairstyleTypeId(), merchantId)
                .orElseThrow(() -> new BusinessException("发型类型不存在"));

        List<HairstyleDetail> selectedDetails = new ArrayList<>();
        BigDecimal additionalPrice = BigDecimal.ZERO;
        int additionalDuration = 0;

        if (request.getHairstyleDetailIds() != null && !request.getHairstyleDetailIds().isEmpty()) {
            for (Long detailId : request.getHairstyleDetailIds()) {
                HairstyleDetail detail = hairstyleDetailRepository.findByIdAndHairstyleTypeId(detailId, request.getHairstyleTypeId())
                        .orElseThrow(() -> new BusinessException("发型细节不存在: " + detailId));
                selectedDetails.add(detail);
                additionalPrice = additionalPrice.add(detail.getAdditionalPrice());
                additionalDuration += detail.getAdditionalMinutes();
            }
        }

        BigDecimal basePrice = hairstyleType.getBasePrice();
        BigDecimal totalPrice = basePrice.add(additionalPrice);

        BigDecimal discountRate = BigDecimal.ONE;
        if (request.getMemberId() != null) {
            Member member = memberRepository.findByIdAndMerchantId(request.getMemberId(), merchantId)
                    .orElse(null);
            if (member != null && member.getMemberLevel() != null) {
                discountRate = member.getMemberLevel().getDiscountRate();
            }
        }

        BigDecimal finalPrice = totalPrice.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountAmount = totalPrice.subtract(finalPrice);

        HairstylePriceResponse response = new HairstylePriceResponse();
        response.setHairstyleTypeId(hairstyleType.getId());
        response.setHairstyleName(hairstyleType.getName());
        response.setBasePrice(basePrice);
        response.setAdditionalPrice(additionalPrice);
        response.setTotalPrice(totalPrice);
        response.setDiscountRate(discountRate);
        response.setDiscountAmount(discountAmount);
        response.setFinalPrice(finalPrice);
        response.setBaseDuration(hairstyleType.getDurationMinutes());
        response.setAdditionalDuration(additionalDuration);
        response.setTotalDuration(hairstyleType.getDurationMinutes() + additionalDuration);

        List<HairstyleDetailResponse> detailResponses = selectedDetails.stream()
                .map(this::convertDetailToResponse)
                .collect(Collectors.toList());
        response.setSelectedDetails(detailResponses);

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
        return response;
    }
}
