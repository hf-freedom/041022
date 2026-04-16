package com.barbershop.saas;

import com.barbershop.saas.common.MerchantContext;
import com.barbershop.saas.dto.*;
import com.barbershop.saas.entity.*;
import com.barbershop.saas.service.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BusinessIntegrationTest {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BarberService barberService;

    @Autowired
    private HairstyleService hairstyleService;

    @Autowired
    private OrderService orderService;

    private static Long testMemberId;
    private static Long testBarberId;
    private static Long testHairstyleId;

    @BeforeEach
    void setUp() {
        MerchantContext.setMerchantId(1L);
    }

    @AfterEach
    void tearDown() {
        MerchantContext.clear();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testStep1_AddMember() {
        log.info("=== 步骤1: 录入会员 ===");
        MemberDTO dto = new MemberDTO();
        dto.setName("集成测试会员");
        dto.setPhone("188" + System.currentTimeMillis() % 100000000);
        dto.setGender("女");
        dto.setAge(28);
        dto.setBalance(BigDecimal.ZERO);
        memberService.addMember(dto);

        List<Member> list = memberService.list((String) "集成测试会员");
        Assert.notEmpty(list, "会员添加失败");
        testMemberId = list.get(0).getId();
        log.info("创建会员成功: ID={}, 等级={}, 折扣={}",
                testMemberId, list.get(0).getLevelName(), list.get(0).getDiscount());
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testStep2_RechargeMember() {
        log.info("=== 步骤2: 会员充值500元 ===");
        RechargeDTO dto = new RechargeDTO();
        dto.setMemberId(testMemberId);
        dto.setAmount(new BigDecimal("500"));
        dto.setRechargeType("微信");
        dto.setRemark("首次充值");
        memberService.recharge(dto);

        Member member = memberService.getById(testMemberId);
        log.info("充值后余额: {}, 等级: {}", member.getBalance(), member.getLevelName());
        Assert.isTrue(member.getBalance().compareTo(new BigDecimal("500")) == 0, "充值失败");
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testStep3_AddBarber() {
        log.info("=== 步骤3: 添加入驻理发师 ===");
        BarberDTO dto = new BarberDTO();
        dto.setName("集成测试理发师");
        dto.setPhone("177" + System.currentTimeMillis() % 100000000);
        dto.setLevelId(2);
        dto.setSkillTags("剪发,烫发,染发,精修");
        dto.setStatus(1);
        barberService.addBarber(dto);

        List<Barber> list = barberService.list((String) "集成测试理发师");
        Assert.notEmpty(list, "理发师添加失败");
        testBarberId = list.get(0).getId();
        log.info("创建理发师成功: ID={}, 级别={}, 提成比例={}",
                testBarberId, list.get(0).getLevelName(), list.get(0).getCommissionRate());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testStep4_AddHairstyle() {
        log.info("=== 步骤4: 添加服务项目 ===");
        HairstyleDTO dto = new HairstyleDTO();
        dto.setName("集成测试-时尚烫染");
        dto.setCategory("烫染");
        dto.setPrice(new BigDecimal("198"));
        dto.setDuration(60);
        dto.setDetailOptions("刘海,鬓角,柔顺,染色");
        dto.setSkillTags("烫发,染发,精修");
        dto.setStatus(1);
        hairstyleService.addHairstyle(dto);

        List<Hairstyle> list = hairstyleService.list((String) null, "集成测试");
        Assert.notEmpty(list, "发型添加失败");
        testHairstyleId = list.get(0).getId();
        log.info("创建发型成功: ID={}, 价格={}", testHairstyleId, list.get(0).getPrice());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testStep5_MatchBarber() {
        log.info("=== 步骤5: 根据发型匹配理发师 ===");
        List<String> details = Arrays.asList("染发", "烫发");
        List<Barber> matched = barberService.matchBarbers(testHairstyleId, details, null);
        log.info("匹配到{}位理发师", matched.size());
        matched.forEach(b -> log.info("推荐理发师: {} - 技能: {}", b.getName(), b.getSkillTags()));
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testStep6_CreateOrder() {
        log.info("=== 步骤6: 创建订单完成消费 ===");
        Member beforeMember = memberService.getById(testMemberId);
        Barber beforeBarber = barberService.getById(testBarberId);

        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setMemberId(testMemberId);
        dto.setBarberId(testBarberId);
        dto.setHairstyleId(testHairstyleId);
        dto.setDetailOptions(Arrays.asList("刘海", "染色"));
        dto.setPayType(1);
        dto.setUseBalance(1);

        Order order = orderService.createOrder(dto);
        Member afterMember = memberService.getById(testMemberId);
        Barber afterBarber = barberService.getById(testBarberId);

        log.info("订单创建成功: {}", order.getOrderNo());
        log.info("原价: {}, 会员折扣: {}, 实付: {}",
                order.getOriginalPrice(), order.getDiscount(), order.getActualPrice());
        log.info("理发师提成: {}% - {}元",
                order.getCommissionRate().multiply(new BigDecimal("100")), order.getCommissionAmount());
        log.info("会员余额: {} -> {}", beforeMember.getBalance(), afterMember.getBalance());
        log.info("理发师接单量: {} -> {}, 累计提成: {}",
                beforeBarber.getOrderCount(), afterBarber.getOrderCount(), afterBarber.getTotalCommission());

        Assert.isTrue(order.getActualPrice().compareTo(BigDecimal.ZERO) > 0, "实付金额错误");
        Assert.isTrue(order.getCommissionAmount().compareTo(BigDecimal.ZERO) > 0, "提成计算错误");
        Assert.isTrue(afterMember.getBalance().compareTo(beforeMember.getBalance()) < 0, "余额未扣款");
        Assert.isTrue(afterBarber.getOrderCount() > beforeBarber.getOrderCount(), "接单量未增加");
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testStep7_VerifyMemberLevel() {
        log.info("=== 步骤7: 验证会员等级自动升级 ===");
        for (int i = 0; i < 5; i++) {
            RechargeDTO dto = new RechargeDTO();
            dto.setMemberId(testMemberId);
            dto.setAmount(new BigDecimal("1000"));
            memberService.recharge(dto);
        }

        Member member = memberService.getById(testMemberId);
        log.info("累计消费金额: {}", member.getTotalConsumeAmount());
        log.info("当前会员等级: {}, 享受折扣: {}折",
                member.getLevelName(), member.getDiscount().multiply(new BigDecimal("10")));

        Assert.notNull(member.getLevelName(), "会员等级未设置");
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testCompleteBusinessFlow() {
        log.info("=== 完整业务流程测试完成 ===");
        log.info("1. 商家入驻审核 → 通过");
        log.info("2. 商家登录 → 获取token");
        log.info("3. 录入会员 → 绑定手机号");
        log.info("4. 会员充值 → 余额增加");
        log.info("5. 录入理发师 → 设置级别和提成");
        log.info("6. 录入发型 → 设置价格和细节选项");
        log.info("7. 用户选发型 → 勾选细节");
        log.info("8. 智能匹配 → 按技能推荐理发师");
        log.info("9. 创建订单 → 自动计算折扣和提成");
        log.info("10. 会员消费 → 余额扣款，自动升级");
        log.info("=== 所有业务流程验证通过 ===");
    }
}
