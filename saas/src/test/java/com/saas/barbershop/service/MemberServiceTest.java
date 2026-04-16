package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberLevelService memberLevelService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantRepository merchantRepository;

    private Long merchantId;

    @BeforeEach
    void setUp() {
        MerchantRegisterRequest merchantRequest = new MerchantRegisterRequest();
        merchantRequest.setShopName("测试理发店");
        merchantRequest.setOwnerName("张三");
        merchantRequest.setPhone("13800138000");
        merchantRequest.setPassword("123456");

        MerchantResponse merchant = merchantService.register(merchantRequest);
        merchantId = merchant.getId();

        MerchantAuditRequest auditRequest = new MerchantAuditRequest();
        auditRequest.setStatus(Merchant.MerchantStatus.APPROVED);
        merchantService.auditMerchant(merchantId, auditRequest);

        MemberLevelRequest levelRequest = new MemberLevelRequest();
        levelRequest.setName("普通会员");
        levelRequest.setLevel(1);
        levelRequest.setMinAmount(BigDecimal.ZERO);
        levelRequest.setDiscountRate(new BigDecimal("1.00"));
        levelRequest.setIsDefault(true);
        memberLevelService.createLevel(merchantId, levelRequest);
    }

    @Test
    void testCreateMember() {
        MemberCreateRequest request = new MemberCreateRequest();
        request.setName("李四");
        request.setPhone("13900139000");
        request.setGender(com.saas.barbershop.entity.Member.Gender.MALE);
        request.setBirthday(LocalDate.of(1990, 1, 1));

        MemberResponse response = memberService.createMember(merchantId, request);

        assertNotNull(response);
        assertEquals("李四", response.getName());
        assertEquals("13900139000", response.getPhone());
        assertEquals(BigDecimal.ZERO, response.getBalance());
    }

    @Test
    void testGetMember() {
        MemberCreateRequest request = new MemberCreateRequest();
        request.setName("李四");
        request.setPhone("13900139000");

        MemberResponse created = memberService.createMember(merchantId, request);
        MemberResponse found = memberService.getMember(merchantId, created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    void testGetAllMembers() {
        MemberCreateRequest request1 = new MemberCreateRequest();
        request1.setName("李四");
        request1.setPhone("13900139000");
        memberService.createMember(merchantId, request1);

        MemberCreateRequest request2 = new MemberCreateRequest();
        request2.setName("王五");
        request2.setPhone("13700137000");
        memberService.createMember(merchantId, request2);

        List<MemberResponse> members = memberService.getAllMembers(merchantId);

        assertEquals(2, members.size());
    }

    @Test
    void testUpdateMember() {
        MemberCreateRequest createRequest = new MemberCreateRequest();
        createRequest.setName("李四");
        createRequest.setPhone("13900139000");
        MemberResponse created = memberService.createMember(merchantId, createRequest);

        MemberUpdateRequest updateRequest = new MemberUpdateRequest();
        updateRequest.setName("李四（已修改）");
        updateRequest.setPhone("13900139000");

        MemberResponse updated = memberService.updateMember(merchantId, created.getId(), updateRequest);

        assertEquals("李四（已修改）", updated.getName());
    }

    @Test
    void testRecharge() {
        MemberCreateRequest request = new MemberCreateRequest();
        request.setName("李四");
        request.setPhone("13900139000");
        MemberResponse member = memberService.createMember(merchantId, request);

        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setAmount(new BigDecimal("100.00"));
        rechargeRequest.setRemark("首次充值");

        MemberResponse recharged = memberService.recharge(merchantId, member.getId(), rechargeRequest);

        assertEquals(new BigDecimal("100.00"), recharged.getBalance());
        assertEquals(new BigDecimal("100.00"), recharged.getTotalRecharge());
    }

    @Test
    void testDeleteMember() {
        MemberCreateRequest request = new MemberCreateRequest();
        request.setName("李四");
        request.setPhone("13900139000");
        MemberResponse member = memberService.createMember(merchantId, request);

        memberService.deleteMember(merchantId, member.getId());

        List<MemberResponse> members = memberService.getAllMembers(merchantId);
        assertTrue(members.isEmpty());
    }
}
