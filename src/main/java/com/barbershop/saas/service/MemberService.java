package com.barbershop.saas.service;

import com.barbershop.saas.common.BusinessException;
import com.barbershop.saas.common.MerchantContext;
import com.barbershop.saas.dto.MemberDTO;
import com.barbershop.saas.dto.RechargeDTO;
import com.barbershop.saas.entity.Member;
import com.barbershop.saas.entity.MemberLevel;
import com.barbershop.saas.entity.RechargeRecord;
import com.barbershop.saas.mapper.MemberLevelMapper;
import com.barbershop.saas.mapper.MemberMapper;
import com.barbershop.saas.mapper.RechargeRecordMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class MemberService extends ServiceImpl<MemberMapper, Member> {

    @Autowired
    private MemberLevelMapper memberLevelMapper;

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    private Long getCurrentMerchantId() {
        Long merchantId = MerchantContext.getMerchantId();
        if (merchantId == null) {
            throw new BusinessException("未获取到商家信息");
        }
        return merchantId;
    }

    public void addMember(MemberDTO dto) {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getMerchantId, merchantId);
        wrapper.eq(Member::getPhone, dto.getPhone());
        Member exist = getOne(wrapper);
        if (exist != null) {
            throw new BusinessException("该手机号已绑定会员");
        }

        Member member = new Member();
        member.setMerchantId(merchantId);
        member.setName(dto.getName());
        member.setPhone(dto.getPhone());
        member.setGender(dto.getGender());
        member.setAge(dto.getAge());
        member.setBalance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO);
        member.setTotalConsumeCount(0);
        member.setTotalConsumeAmount(BigDecimal.ZERO);
        setMemberLevel(member);
        save(member);
    }

    public void bindPhone(Long memberId, String phone) {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getMerchantId, merchantId);
        wrapper.eq(Member::getPhone, phone);
        Member exist = getOne(wrapper);
        if (exist != null && !exist.getId().equals(memberId)) {
            throw new BusinessException("该手机号已绑定其他会员");
        }

        Member member = getById(memberId);
        if (member == null || !member.getMerchantId().equals(merchantId)) {
            throw new BusinessException("会员不存在");
        }
        member.setPhone(phone);
        updateById(member);
    }

    @Transactional(rollbackFor = Exception.class)
    public void recharge(RechargeDTO dto) {
        Long merchantId = getCurrentMerchantId();
        Member member = getById(dto.getMemberId());
        if (member == null || !member.getMerchantId().equals(merchantId)) {
            throw new BusinessException("会员不存在");
        }

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }

        BigDecimal beforeBalance = member.getBalance();
        BigDecimal afterBalance = beforeBalance.add(dto.getAmount());
        member.setBalance(afterBalance);
        setMemberLevel(member);
        updateById(member);

        RechargeRecord record = new RechargeRecord();
        record.setMerchantId(merchantId);
        record.setMemberId(member.getId());
        record.setMemberName(member.getName());
        record.setMemberPhone(member.getPhone());
        record.setAmount(dto.getAmount());
        record.setBeforeBalance(beforeBalance);
        record.setAfterBalance(afterBalance);
        record.setRechargeType(dto.getRechargeType());
        record.setRemark(dto.getRemark());
        rechargeRecordMapper.insert(record);
    }

    public void consume(Long memberId, BigDecimal amount) {
        Long merchantId = getCurrentMerchantId();
        Member member = getById(memberId);
        if (member == null || !member.getMerchantId().equals(merchantId)) {
            throw new BusinessException("会员不存在");
        }

        if (member.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }

        member.setBalance(member.getBalance().subtract(amount));
        member.setTotalConsumeCount(member.getTotalConsumeCount() + 1);
        member.setTotalConsumeAmount(member.getTotalConsumeAmount().add(amount));
        setMemberLevel(member);
        updateById(member);
    }

    private void setMemberLevel(Member member) {
        Long merchantId = member.getMerchantId();
        LambdaQueryWrapper<MemberLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberLevel::getMerchantId, merchantId);
        List<MemberLevel> levels = memberLevelMapper.selectList(wrapper);

        if (levels.isEmpty()) {
            initDefaultLevels(merchantId);
            levels = memberLevelMapper.selectList(wrapper);
        }

        MemberLevel currentLevel = levels.stream()
                .filter(level -> member.getTotalConsumeAmount().compareTo(level.getMinAmount()) >= 0)
                .max(Comparator.comparing(MemberLevel::getMinAmount))
                .orElse(levels.get(0));

        member.setLevelId(currentLevel.getId().intValue());
        member.setLevelName(currentLevel.getName());
        member.setDiscount(currentLevel.getDiscount());
    }

    private void initDefaultLevels(Long merchantId) {
        String[] levelNames = {"普通会员", "银卡会员", "金卡会员", "钻石会员"};
        BigDecimal[] discounts = {new BigDecimal("1.00"), new BigDecimal("0.90"), new BigDecimal("0.85"), new BigDecimal("0.80")};
        BigDecimal[] minAmounts = {BigDecimal.ZERO, new BigDecimal("500"), new BigDecimal("2000"), new BigDecimal("5000")};

        for (int i = 0; i < levelNames.length; i++) {
            MemberLevel level = new MemberLevel();
            level.setMerchantId(merchantId);
            level.setName(levelNames[i]);
            level.setDiscount(discounts[i]);
            level.setMinAmount(minAmounts[i]);
            level.setMinCount(0);
            memberLevelMapper.insert(level);
        }
    }

    public List<Member> list(String keyword) {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Member::getMerchantId, merchantId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Member::getName, keyword).or().like(Member::getPhone, keyword));
        }
        wrapper.orderByDesc(Member::getCreateTime);
        return list(wrapper);
    }

    public List<MemberLevel> listLevels() {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<MemberLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberLevel::getMerchantId, merchantId);
        wrapper.orderByAsc(MemberLevel::getMinAmount);
        return memberLevelMapper.selectList(wrapper);
    }
}
