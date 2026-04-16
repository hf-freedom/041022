package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.Member;
import com.saas.barbershop.entity.MemberLevel;
import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.entity.TransactionRecord;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.MemberLevelRepository;
import com.saas.barbershop.repository.MemberRepository;
import com.saas.barbershop.repository.MerchantRepository;
import com.saas.barbershop.repository.TransactionRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberLevelRepository memberLevelRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Transactional
    public MemberResponse createMember(Long merchantId, MemberCreateRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在"));

        memberRepository.findByMerchantIdAndPhone(merchantId, request.getPhone())
                .ifPresent(m -> {
                    throw new BusinessException("该手机号已绑定会员");
                });

        Member member = new Member();
        member.setMerchant(merchant);
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setGender(request.getGender());
        member.setBirthday(request.getBirthday());
        member.setRemark(request.getRemark());

        MemberLevel defaultLevel = memberLevelRepository.findDefaultByMerchantId(merchantId)
                .orElseThrow(() -> new BusinessException("未设置默认会员等级"));
        member.setMemberLevel(defaultLevel);

        Member savedMember = memberRepository.save(member);
        return convertToResponse(savedMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long merchantId, Long memberId) {
        Member member = memberRepository.findByIdAndMerchantId(memberId, merchantId)
                .orElseThrow(() -> new BusinessException("会员不存在"));
        return convertToResponse(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberByPhone(Long merchantId, String phone) {
        Member member = memberRepository.findByMerchantIdAndPhone(merchantId, phone)
                .orElseThrow(() -> new BusinessException("会员不存在"));
        return convertToResponse(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers(Long merchantId) {
        return memberRepository.findByMerchantId(merchantId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MemberResponse> getMembers(Long merchantId, Pageable pageable) {
        return memberRepository.findByMerchantId(merchantId, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<MemberResponse> searchMembers(Long merchantId, String keyword, Pageable pageable) {
        return memberRepository.findByMerchantIdAndKeyword(merchantId, keyword, pageable)
                .map(this::convertToResponse);
    }

    @Transactional
    public MemberResponse updateMember(Long merchantId, Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findByIdAndMerchantId(memberId, merchantId)
                .orElseThrow(() -> new BusinessException("会员不存在"));

        if (!member.getPhone().equals(request.getPhone())) {
            memberRepository.findByMerchantIdAndPhone(merchantId, request.getPhone())
                    .ifPresent(m -> {
                        throw new BusinessException("该手机号已绑定其他会员");
                    });
        }

        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setGender(request.getGender());
        member.setBirthday(request.getBirthday());
        member.setRemark(request.getRemark());

        Member updatedMember = memberRepository.save(member);
        return convertToResponse(updatedMember);
    }

    @Transactional
    public MemberResponse recharge(Long merchantId, Long memberId, RechargeRequest request) {
        Member member = memberRepository.findByIdAndMerchantId(memberId, merchantId)
                .orElseThrow(() -> new BusinessException("会员不存在"));

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }

        BigDecimal balanceBefore = member.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(request.getAmount());

        member.setBalance(balanceAfter);
        member.setTotalRecharge(member.getTotalRecharge().add(request.getAmount()));

        updateMemberLevel(member);

        Member updatedMember = memberRepository.save(member);

        TransactionRecord record = new TransactionRecord();
        record.setMerchant(member.getMerchant());
        record.setMember(member);
        record.setType(TransactionRecord.TransactionType.RECHARGE);
        record.setAmount(request.getAmount());
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setRemark(request.getRemark());
        transactionRecordRepository.save(record);

        return convertToResponse(updatedMember);
    }

    @Transactional
    public void deleteMember(Long merchantId, Long memberId) {
        Member member = memberRepository.findByIdAndMerchantId(memberId, merchantId)
                .orElseThrow(() -> new BusinessException("会员不存在"));

        member.setIsDeleted(true);
        memberRepository.save(member);
    }

    private void updateMemberLevel(Member member) {
        List<MemberLevel> levels = memberLevelRepository.findByMerchantIdOrderByLevelAsc(member.getMerchant().getId());

        MemberLevel currentLevel = member.getMemberLevel();
        MemberLevel newLevel = currentLevel;

        for (MemberLevel level : levels) {
            if (member.getTotalConsumption().compareTo(level.getMinAmount()) >= 0) {
                newLevel = level;
            }
        }

        member.setMemberLevel(newLevel);
    }

    private MemberResponse convertToResponse(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setName(member.getName());
        response.setPhone(member.getPhone());
        response.setGender(member.getGender());
        response.setBirthday(member.getBirthday());
        response.setAvatarUrl(member.getAvatarUrl());
        response.setBalance(member.getBalance());
        response.setTotalConsumption(member.getTotalConsumption());
        response.setTotalRecharge(member.getTotalRecharge());

        if (member.getMemberLevel() != null) {
            MemberLevelResponse levelResponse = new MemberLevelResponse();
            levelResponse.setId(member.getMemberLevel().getId());
            levelResponse.setName(member.getMemberLevel().getName());
            levelResponse.setLevel(member.getMemberLevel().getLevel());
            levelResponse.setDiscountRate(member.getMemberLevel().getDiscountRate());
            response.setMemberLevel(levelResponse);
        }

        response.setRemark(member.getRemark());
        response.setLastConsumptionAt(member.getLastConsumptionAt());
        response.setCreatedAt(member.getCreatedAt());
        return response;
    }
}
