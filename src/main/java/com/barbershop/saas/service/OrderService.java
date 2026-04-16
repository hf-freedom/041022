package com.barbershop.saas.service;

import com.barbershop.saas.common.BusinessException;
import com.barbershop.saas.common.MerchantContext;
import com.barbershop.saas.dto.CreateOrderDTO;
import com.barbershop.saas.entity.Barber;
import com.barbershop.saas.entity.Hairstyle;
import com.barbershop.saas.entity.Member;
import com.barbershop.saas.entity.Order;
import com.barbershop.saas.mapper.BarberMapper;
import com.barbershop.saas.mapper.HairstyleMapper;
import com.barbershop.saas.mapper.MemberMapper;
import com.barbershop.saas.mapper.OrderMapper;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private BarberMapper barberMapper;

    @Autowired
    private HairstyleMapper hairstyleMapper;

    @Autowired
    private MemberService memberService;

    private Long getCurrentMerchantId() {
        Long merchantId = MerchantContext.getMerchantId();
        if (merchantId == null) {
            throw new BusinessException("未获取到商家信息");
        }
        return merchantId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(CreateOrderDTO dto) {
        Long merchantId = getCurrentMerchantId();

        Barber barber = barberMapper.selectById(dto.getBarberId());
        if (barber == null || !barber.getMerchantId().equals(merchantId)) {
            throw new BusinessException("理发师不存在");
        }

        Hairstyle hairstyle = hairstyleMapper.selectById(dto.getHairstyleId());
        if (hairstyle == null || !hairstyle.getMerchantId().equals(merchantId)) {
            throw new BusinessException("发型不存在");
        }

        Member member = null;
        BigDecimal discount = new BigDecimal("1.00");
        if (dto.getMemberId() != null) {
            member = memberMapper.selectById(dto.getMemberId());
            if (member == null || !member.getMerchantId().equals(merchantId)) {
                throw new BusinessException("会员不存在");
            }
            discount = member.getDiscount();
        }

        BigDecimal originalPrice = hairstyle.getPrice();
        BigDecimal actualPrice = originalPrice.multiply(discount);
        BigDecimal balanceAmount = BigDecimal.ZERO;

        if (dto.getUseBalance() != null && dto.getUseBalance() == 1 && member != null) {
            if (member.getBalance().compareTo(actualPrice) < 0) {
                throw new BusinessException("会员余额不足");
            }
            balanceAmount = actualPrice;
            memberService.consume(member.getId(), balanceAmount);
            member = memberMapper.selectById(member.getId());
        }

        BigDecimal commissionAmount = actualPrice.multiply(barber.getCommissionRate());

        Order order = new Order();
        order.setMerchantId(merchantId);
        order.setOrderNo(generateOrderNo());
        if (member != null) {
            order.setMemberId(member.getId());
            order.setMemberName(member.getName());
            order.setMemberPhone(member.getPhone());
        }
        order.setBarberId(barber.getId());
        order.setBarberName(barber.getName());
        order.setHairstyleId(hairstyle.getId());
        order.setHairstyleName(hairstyle.getName());
        if (dto.getDetailOptions() != null && !dto.getDetailOptions().isEmpty()) {
            StringJoiner joiner = new StringJoiner(",");
            dto.getDetailOptions().forEach(joiner::add);
            order.setDetailOptions(joiner.toString());
        }
        order.setOriginalPrice(originalPrice);
        order.setDiscount(discount);
        order.setActualPrice(actualPrice);
        order.setPayType(dto.getPayType());
        order.setUseBalance(dto.getUseBalance());
        order.setBalanceAmount(balanceAmount);
        order.setStatus(1);
        order.setCommissionRate(barber.getCommissionRate());
        order.setCommissionAmount(commissionAmount);
        order.setRemark(dto.getRemark());
        save(order);

        barber.setOrderCount(barber.getOrderCount() + 1);
        barber.setTotalCommission(barber.getTotalCommission().add(commissionAmount));
        barberMapper.updateById(barber);

        return order;
    }

    private String generateOrderNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = IdUtil.randomUUID().substring(0, 8).toUpperCase();
        return "ORD" + date + random;
    }

    public List<Order> list(String keyword, Integer status) {
        Long merchantId = getCurrentMerchantId();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getMerchantId, merchantId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Order::getMemberName, keyword)
                    .or().like(Order::getMemberPhone, keyword)
                    .or().like(Order::getBarberName, keyword)
                    .or().like(Order::getOrderNo, keyword));
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return list(wrapper);
    }

    public Order getDetail(Long id) {
        Long merchantId = getCurrentMerchantId();
        Order order = getById(id);
        if (order == null || !order.getMerchantId().equals(merchantId)) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }
}
