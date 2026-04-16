package com.barbershop.saas;

import com.barbershop.saas.common.MerchantContext;
import com.barbershop.saas.dto.MemberDTO;
import com.barbershop.saas.dto.RechargeDTO;
import com.barbershop.saas.entity.Member;
import com.barbershop.saas.entity.MemberLevel;
import com.barbershop.saas.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@SpringBootTest
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MerchantContext.setMerchantId(1L);
    }

    @AfterEach
    void tearDown() {
        MerchantContext.clear();
    }

    @Test
    void testAddMember() {
        MemberDTO dto = new MemberDTO();
        dto.setName("测试会员" + System.currentTimeMillis());
        dto.setPhone("139" + System.currentTimeMillis() % 100000000);
        dto.setGender("男");
        dto.setAge(25);
        dto.setBalance(BigDecimal.ZERO);

        memberService.addMember(dto);
        log.info("会员添加成功");
    }

    @Test
    void testRecharge() {
        List<Member> list = memberService.list((String) null);
        if (!list.isEmpty()) {
            Member member = list.get(0);
            BigDecimal beforeBalance = member.getBalance();

            RechargeDTO dto = new RechargeDTO();
            dto.setMemberId(member.getId());
            dto.setAmount(new BigDecimal("500"));
            dto.setRechargeType("微信");
            dto.setRemark("测试充值");

            memberService.recharge(dto);

            Member updated = memberService.getById(member.getId());
            log.info("充值前: {}, 充值后: {}", beforeBalance, updated.getBalance());
            Assert.isTrue(updated.getBalance().compareTo(beforeBalance.add(new BigDecimal("500"))) == 0, "充值金额错误");
        }
    }

    @Test
    void testConsume() {
        List<Member> list = memberService.list((String) null);
        if (!list.isEmpty()) {
            Member member = list.get(0);
            if (member.getBalance().compareTo(new BigDecimal("50")) >= 0) {
                BigDecimal beforeBalance = member.getBalance();
                memberService.consume(member.getId(), new BigDecimal("50"));

                Member updated = memberService.getById(member.getId());
                log.info("消费前: {}, 消费后: {}", beforeBalance, updated.getBalance());
                Assert.isTrue(updated.getBalance().compareTo(beforeBalance.subtract(new BigDecimal("50"))) == 0, "消费金额错误");
                Assert.isTrue(updated.getTotalConsumeCount() > 0, "消费次数未增加");
            }
        }
    }

    @Test
    void testMemberLevel() {
        List<MemberLevel> levels = memberService.listLevels();
        log.info("会员等级数量: {}", levels.size());
        levels.forEach(level -> log.info("等级: {}, 折扣: {}, 最低消费: {}",
                level.getName(), level.getDiscount(), level.getMinAmount()));
        Assert.notEmpty(levels, "会员等级不能为空");
    }

    @Test
    void testListMembers() {
        List<Member> list = memberService.list((String) null);
        log.info("会员数量: {}", list.size());
        list.forEach(m -> log.info("会员: {}, 等级: {}, 折扣: {}, 余额: {}",
                m.getName(), m.getLevelName(), m.getDiscount(), m.getBalance()));
    }

    @Test
    void testBindPhone() {
        List<Member> list = memberService.list((String) null);
        if (!list.isEmpty()) {
            Member member = list.get(0);
            String newPhone = "138" + System.currentTimeMillis() % 100000000;
            memberService.bindPhone(member.getId(), newPhone);
            Member updated = memberService.getById(member.getId());
            log.info("绑定新手机号: {}", updated.getPhone());
            Assert.isTrue(updated.getPhone().equals(newPhone), "手机号绑定错误");
        }
    }
}
