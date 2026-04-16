package com.barbershop.saas;

import com.barbershop.saas.common.MerchantContext;
import com.barbershop.saas.dto.CreateOrderDTO;
import com.barbershop.saas.entity.Barber;
import com.barbershop.saas.entity.Hairstyle;
import com.barbershop.saas.entity.Member;
import com.barbershop.saas.entity.Order;
import com.barbershop.saas.service.BarberService;
import com.barbershop.saas.service.HairstyleService;
import com.barbershop.saas.service.MemberService;
import com.barbershop.saas.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BarberService barberService;

    @Autowired
    private HairstyleService hairstyleService;

    @BeforeEach
    void setUp() {
        MerchantContext.setMerchantId(1L);
    }

    @AfterEach
    void tearDown() {
        MerchantContext.clear();
    }

    @Test
    void testCreateOrderForGuest() {
        List<Barber> barbers = barberService.list((String) null);
        List<Hairstyle> hairstyles = hairstyleService.list(null, null);

        if (!barbers.isEmpty() && !hairstyles.isEmpty()) {
            CreateOrderDTO dto = new CreateOrderDTO();
            dto.setBarberId(barbers.get(0).getId());
            dto.setHairstyleId(hairstyles.get(0).getId());
            dto.setDetailOptions(Arrays.asList("刘海", "鬓角"));
            dto.setPayType(1);
            dto.setUseBalance(0);
            dto.setRemark("散客订单");

            Barber beforeBarber = barberService.getById(barbers.get(0).getId());
            Integer beforeOrderCount = beforeBarber.getOrderCount();

            Order order = orderService.createOrder(dto);
            log.info("创建散客订单成功: {}, 原价: {}, 实付: {}, 提成: {}",
                    order.getOrderNo(), order.getOriginalPrice(),
                    order.getActualPrice(), order.getCommissionAmount());

            Barber afterBarber = barberService.getById(barbers.get(0).getId());
            Assert.isTrue(afterBarber.getOrderCount().equals(beforeOrderCount + 1), "理发师接单数量未增加");
        }
    }

    @Test
    void testCreateOrderForMember() {
        List<Member> members = memberService.list((String) null);
        List<Barber> barbers = barberService.list((String) null);
        List<Hairstyle> hairstyles = hairstyleService.list(null, null);

        if (!members.isEmpty() && !barbers.isEmpty() && !hairstyles.isEmpty()) {
            Member member = members.get(0);
            CreateOrderDTO dto = new CreateOrderDTO();
            dto.setMemberId(member.getId());
            dto.setBarberId(barbers.get(0).getId());
            dto.setHairstyleId(hairstyles.get(0).getId());
            dto.setPayType(1);
            dto.setUseBalance(0);
            dto.setRemark("会员订单");

            Order order = orderService.createOrder(dto);
            log.info("创建会员订单成功: {}, 会员折扣: {}, 原价: {}, 实付: {}",
                    order.getOrderNo(), order.getDiscount(),
                    order.getOriginalPrice(), order.getActualPrice());

            Assert.isTrue(order.getDiscount().compareTo(member.getDiscount()) == 0, "会员折扣计算错误");
            Assert.isTrue(order.getActualPrice().compareTo(
                    order.getOriginalPrice().multiply(member.getDiscount())) == 0, "实付金额计算错误");
        }
    }

    @Test
    void testCreateOrderWithBalance() {
        List<Member> members = memberService.list((String) null);
        List<Barber> barbers = barberService.list((String) null);
        List<Hairstyle> hairstyles = hairstyleService.list(null, null);

        if (!members.isEmpty() && !barbers.isEmpty() && !hairstyles.isEmpty()) {
            Member member = members.stream()
                    .filter(m -> m.getBalance().compareTo(new BigDecimal("100")) >= 0)
                    .findFirst()
                    .orElse(null);

            if (member != null) {
                BigDecimal beforeBalance = member.getBalance();

                CreateOrderDTO dto = new CreateOrderDTO();
                dto.setMemberId(member.getId());
                dto.setBarberId(barbers.get(0).getId());
                dto.setHairstyleId(hairstyles.get(0).getId());
                dto.setPayType(1);
                dto.setUseBalance(1);

                Order order = orderService.createOrder(dto);
                Member updatedMember = memberService.getById(member.getId());

                log.info("使用余额支付: 订单金额{}, 支付前余额{}, 支付后余额{}",
                        order.getActualPrice(), beforeBalance, updatedMember.getBalance());

                Assert.isTrue(order.getBalanceAmount().compareTo(BigDecimal.ZERO) > 0, "余额支付金额未记录");
            }
        }
    }

    @Test
    void testListOrders() {
        List<Order> list = orderService.list((String) null, (Integer) null);
        log.info("订单数量: {}", list.size());
        list.forEach(o -> log.info("订单: {}, 理发师: {}, 会员: {}, 实付: {}, 提成: {}",
                o.getOrderNo(), o.getBarberName(), o.getMemberName(),
                o.getActualPrice(), o.getCommissionAmount()));
    }

    @Test
    void testGetOrderDetail() {
        List<Order> list = orderService.list((String) null, (Integer) null);
        if (!list.isEmpty()) {
            Order order = orderService.getDetail(list.get(0).getId());
            log.info("订单详情: {}", order);
            Assert.notNull(order.getOrderNo(), "订单号不能为空");
        }
    }
}
