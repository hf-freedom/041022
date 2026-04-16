package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.*;
import com.saas.barbershop.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class MatchingServiceTest {

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MemberLevelService memberLevelService;

    @Autowired
    private BarberLevelService barberLevelService;

    @Autowired
    private BarberService barberService;

    @Autowired
    private HairstyleService hairstyleService;

    @Autowired
    private BarberHairstyleService barberHairstyleService;

    @Autowired
    private MemberService memberService;

    private Long merchantId;
    private Long hairstyleTypeId;
    private Long barberId;
    private Long memberId;

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
        levelRequest.setDiscountRate(new BigDecimal("0.90"));
        levelRequest.setIsDefault(true);
        memberLevelService.createLevel(merchantId, levelRequest);

        BarberLevelRequest barberLevelRequest = new BarberLevelRequest();
        barberLevelRequest.setName("高级理发师");
        barberLevelRequest.setLevel(1);
        barberLevelRequest.setCommissionRate(new BigDecimal("0.30"));
        barberLevelRequest.setIsDefault(true);
        barberLevelService.createLevel(merchantId, barberLevelRequest);

        BarberCreateRequest barberRequest = new BarberCreateRequest();
        barberRequest.setName("王师傅");
        barberRequest.setPhone("13600136000");
        barberRequest.setGender(Barber.Gender.MALE);
        BarberResponse barber = barberService.createBarber(merchantId, barberRequest);
        barberId = barber.getId();

        HairstyleTypeRequest typeRequest = new HairstyleTypeRequest();
        typeRequest.setName("男士剪发");
        typeRequest.setCategory(HairstyleType.Category.CUT);
        typeRequest.setBasePrice(new BigDecimal("50.00"));
        typeRequest.setDurationMinutes(30);
        HairstyleTypeResponse type = hairstyleService.createHairstyleType(merchantId, typeRequest);
        hairstyleTypeId = type.getId();

        HairstyleDetailRequest detailRequest = new HairstyleDetailRequest();
        detailRequest.setName("洗头");
        detailRequest.setAdditionalPrice(new BigDecimal("10.00"));
        detailRequest.setAdditionalMinutes(10);
        hairstyleService.createHairstyleDetail(merchantId, hairstyleTypeId, detailRequest);

        BarberHairstyleRequest bhRequest = new BarberHairstyleRequest();
        bhRequest.setBarberId(barberId);
        bhRequest.setHairstyleTypeId(hairstyleTypeId);
        barberHairstyleService.addBarberHairstyle(merchantId, bhRequest);

        MemberCreateRequest memberRequest = new MemberCreateRequest();
        memberRequest.setName("李四");
        memberRequest.setPhone("13900139000");
        MemberResponse member = memberService.createMember(merchantId, memberRequest);
        memberId = member.getId();
    }

    @Test
    void testMatchBarbers() {
        BarberMatchRequest request = new BarberMatchRequest();
        request.setHairstyleTypeId(hairstyleTypeId);
        request.setMemberId(memberId);

        List<BarberMatchResponse> matches = matchingService.matchBarbers(merchantId, request);

        assertNotNull(matches);
        assertFalse(matches.isEmpty());

        BarberMatchResponse match = matches.get(0);
        assertEquals(barberId, match.getBarberId());
        assertEquals(new BigDecimal("50.00"), match.getBasePrice());
        assertEquals(new BigDecimal("0.90"), match.getDiscountRate());
        assertEquals(new BigDecimal("45.00"), match.getFinalPrice());
    }

    @Test
    void testMatchBarbersWithDetails() {
        List<HairstyleDetailResponse> details = hairstyleService.getHairstyleDetails(merchantId, hairstyleTypeId);
        Long detailId = details.get(0).getId();

        BarberMatchRequest request = new BarberMatchRequest();
        request.setHairstyleTypeId(hairstyleTypeId);
        request.setHairstyleDetailIds(Arrays.asList(detailId));
        request.setMemberId(memberId);

        List<BarberMatchResponse> matches = matchingService.matchBarbers(merchantId, request);

        assertNotNull(matches);
        assertFalse(matches.isEmpty());

        BarberMatchResponse match = matches.get(0);
        assertEquals(new BigDecimal("50.00"), match.getBasePrice());
        assertEquals(new BigDecimal("10.00"), match.getAdditionalPrice());
        assertEquals(new BigDecimal("60.00"), match.getTotalPrice());
        assertEquals(new BigDecimal("54.00"), match.getFinalPrice());
    }

    @Test
    void testCalculatePrice() {
        HairstylePriceRequest request = new HairstylePriceRequest();
        request.setHairstyleTypeId(hairstyleTypeId);
        request.setMemberId(memberId);

        HairstylePriceResponse response = matchingService.calculatePrice(merchantId, request);

        assertNotNull(response);
        assertEquals(new BigDecimal("50.00"), response.getBasePrice());
        assertEquals(new BigDecimal("0.90"), response.getDiscountRate());
        assertEquals(new BigDecimal("45.00"), response.getFinalPrice());
    }
}
