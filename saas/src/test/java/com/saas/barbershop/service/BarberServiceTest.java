package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.Barber;
import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.BarberRepository;
import com.saas.barbershop.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class BarberServiceTest {

    @Autowired
    private BarberService barberService;

    @Autowired
    private BarberLevelService barberLevelService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    private Long merchantId;
    private Long barberLevelId;

    @BeforeEach
    void setUp() {
        // 创建商家
        MerchantRegisterRequest merchantRequest = new MerchantRegisterRequest();
        merchantRequest.setShopName("测试理发店");
        merchantRequest.setOwnerName("张老板");
        merchantRequest.setPhone("13800138000");
        merchantRequest.setPassword("123456");
        merchantRequest.setEmail("test@example.com");
        MerchantResponse merchant = merchantService.register(merchantRequest);
        merchantId = merchant.getId();

        // 审核商家
        Merchant merchantEntity = merchantRepository.findById(merchantId).get();
        merchantEntity.setStatus(Merchant.MerchantStatus.APPROVED);
        merchantRepository.save(merchantEntity);

        // 创建理发师级别
        BarberLevelRequest levelRequest = new BarberLevelRequest();
        levelRequest.setName("高级理发师");
        levelRequest.setLevel(1);
        levelRequest.setCommissionRate(new java.math.BigDecimal("0.30"));
        levelRequest.setDescription("提成30%");
        levelRequest.setIsDefault(true);
        BarberLevelResponse barberLevel = barberLevelService.createBarberLevel(merchantId, levelRequest);
        barberLevelId = barberLevel.getId();
    }

    @Test
    void testCreateBarber() {
        BarberCreateRequest request = new BarberCreateRequest();
        request.setName("王师傅");
        request.setPhone("13600136000");
        request.setGender("MALE");
        request.setBirthday(LocalDate.of(1985, 5, 1));
        request.setEntryDate(LocalDate.of(2020, 1, 1));
        request.setLevelId(barberLevelId);
        request.setSpecialties("剪发、烫发");
        request.setIntroduction("10年经验高级理发师");

        BarberResponse response = barberService.createBarber(merchantId, request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("王师傅", response.getName());
        assertEquals("13600136000", response.getPhone());
        assertEquals(Barber.Gender.MALE, response.getGender());
        assertEquals("高级理发师", response.getLevelName());
        assertEquals(0, response.getOrderCount());
        assertTrue(response.getIsActive());
    }

    @Test
    void testCreateBarberWithDuplicatePhone() {
        // 创建第一个理发师
        BarberCreateRequest request1 = new BarberCreateRequest();
        request1.setName("王师傅");
        request1.setPhone("13600136000");
        request1.setGender("MALE");
        request1.setEntryDate(LocalDate.of(2020, 1, 1));
        request1.setLevelId(barberLevelId);
        barberService.createBarber(merchantId, request1);

        // 使用相同手机号创建第二个理发师应该失败
        BarberCreateRequest request2 = new BarberCreateRequest();
        request2.setName("李师傅");
        request2.setPhone("13600136000"); // 相同手机号
        request2.setGender("MALE");
        request2.setEntryDate(LocalDate.of(2021, 1, 1));
        request2.setLevelId(barberLevelId);

        assertThrows(BusinessException.class, () -> {
            barberService.createBarber(merchantId, request2);
        });
    }

    @Test
    void testGetBarberById() {
        BarberCreateRequest request = new BarberCreateRequest();
        request.setName("王师傅");
        request.setPhone("13600136000");
        request.setGender("MALE");
        request.setEntryDate(LocalDate.of(2020, 1, 1));
        request.setLevelId(barberLevelId);
        BarberResponse created = barberService.createBarber(merchantId, request);

        BarberResponse retrieved = barberService.getBarber(merchantId, created.getId());

        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals("王师傅", retrieved.getName());
    }

    @Test
    void testGetBarberNotFound() {
        assertThrows(BusinessException.class, () -> {
            barberService.getBarber(merchantId, 99999L);
        });
    }

    @Test
    void testGetAllBarbers() {
        // 创建多个理发师
        for (int i = 0; i < 3; i++) {
            BarberCreateRequest request = new BarberCreateRequest();
            request.setName("理发师" + i);
            request.setPhone("1360013600" + i);
            request.setGender(i % 2 == 0 ? "MALE" : "FEMALE");
            request.setEntryDate(LocalDate.of(2020, 1, 1));
            request.setLevelId(barberLevelId);
            barberService.createBarber(merchantId, request);
        }

        List<BarberResponse> barbers = barberService.getAllBarbers(merchantId);

        assertEquals(3, barbers.size());
    }

    @Test
    void testGetActiveBarbers() {
        // 创建在职理发师
        BarberCreateRequest request1 = new BarberCreateRequest();
        request1.setName("在职理发师");
        request1.setPhone("13600136000");
        request1.setGender("MALE");
        request1.setEntryDate(LocalDate.of(2020, 1, 1));
        request1.setLevelId(barberLevelId);
        request1.setIsActive(true);
        barberService.createBarber(merchantId, request1);

        // 创建离职理发师
        BarberCreateRequest request2 = new BarberCreateRequest();
        request2.setName("离职理发师");
        request2.setPhone("13600136001");
        request2.setGender("MALE");
        request2.setEntryDate(LocalDate.of(2020, 1, 1));
        request2.setLevelId(barberLevelId);
        BarberResponse inactiveBarber = barberService.createBarber(merchantId, request2);

        // 设置为离职
        BarberUpdateRequest updateRequest = new BarberUpdateRequest();
        updateRequest.setName("离职理发师");
        updateRequest.setPhone("13600136001");
        updateRequest.setGender("MALE");
        updateRequest.setLevelId(barberLevelId);
        updateRequest.setIsActive(false);
        barberService.updateBarber(merchantId, inactiveBarber.getId(), updateRequest);

        List<BarberResponse> activeBarbers = barberService.getActiveBarbers(merchantId);

        assertEquals(1, activeBarbers.size());
        assertEquals("在职理发师", activeBarbers.get(0).getName());
    }

    @Test
    void testUpdateBarber() {
        BarberCreateRequest createRequest = new BarberCreateRequest();
        createRequest.setName("王师傅");
        createRequest.setPhone("13600136000");
        createRequest.setGender("MALE");
        createRequest.setEntryDate(LocalDate.of(2020, 1, 1));
        createRequest.setLevelId(barberLevelId);
        BarberResponse created = barberService.createBarber(merchantId, createRequest);

        BarberUpdateRequest updateRequest = new BarberUpdateRequest();
        updateRequest.setName("王大师");
        updateRequest.setPhone("13600136000");
        updateRequest.setGender("MALE");
        updateRequest.setLevelId(barberLevelId);
        updateRequest.setSpecialties("剪发、烫发、染发");
        updateRequest.setIntroduction("15年经验");

        BarberResponse updated = barberService.updateBarber(merchantId, created.getId(), updateRequest);

        assertEquals("王大师", updated.getName());
        assertEquals("剪发、烫发、染发", updated.getSpecialties());
    }

    @Test
    void testUpdateBarberPhone() {
        BarberCreateRequest createRequest = new BarberCreateRequest();
        createRequest.setName("王师傅");
        createRequest.setPhone("13600136000");
        createRequest.setGender("MALE");
        createRequest.setEntryDate(LocalDate.of(2020, 1, 1));
        createRequest.setLevelId(barberLevelId);
        BarberResponse created = barberService.createBarber(merchantId, createRequest);

        BarberUpdateRequest updateRequest = new BarberUpdateRequest();
        updateRequest.setName("王师傅");
        updateRequest.setPhone("13600136111"); // 修改手机号
        updateRequest.setGender("MALE");
        updateRequest.setLevelId(barberLevelId);

        BarberResponse updated = barberService.updateBarber(merchantId, created.getId(), updateRequest);

        assertEquals("13600136111", updated.getPhone());
    }

    @Test
    void testDeleteBarber() {
        BarberCreateRequest request = new BarberCreateRequest();
        request.setName("王师傅");
        request.setPhone("13600136000");
        request.setGender("MALE");
        request.setEntryDate(LocalDate.of(2020, 1, 1));
        request.setLevelId(barberLevelId);
        BarberResponse created = barberService.createBarber(merchantId, request);

        barberService.deleteBarber(merchantId, created.getId());

        // 删除后应该查找不到
        assertThrows(BusinessException.class, () -> {
            barberService.getBarber(merchantId, created.getId());
        });
    }

    @Test
    void testCreateBarberWithDifferentGenders() {
        // 男性理发师
        BarberCreateRequest maleRequest = new BarberCreateRequest();
        maleRequest.setName("男理发师");
        maleRequest.setPhone("13600136000");
        maleRequest.setGender("MALE");
        maleRequest.setEntryDate(LocalDate.of(2020, 1, 1));
        maleRequest.setLevelId(barberLevelId);
        BarberResponse maleBarber = barberService.createBarber(merchantId, maleRequest);
        assertEquals(Barber.Gender.MALE, maleBarber.getGender());

        // 女性理发师
        BarberCreateRequest femaleRequest = new BarberCreateRequest();
        femaleRequest.setName("女理发师");
        femaleRequest.setPhone("13600136001");
        femaleRequest.setGender("FEMALE");
        femaleRequest.setEntryDate(LocalDate.of(2020, 1, 1));
        femaleRequest.setLevelId(barberLevelId);
        BarberResponse femaleBarber = barberService.createBarber(merchantId, femaleRequest);
        assertEquals(Barber.Gender.FEMALE, femaleBarber.getGender());
    }

    @Test
    void testBarberOrderCountIncrement() {
        BarberCreateRequest request = new BarberCreateRequest();
        request.setName("王师傅");
        request.setPhone("13600136000");
        request.setGender("MALE");
        request.setEntryDate(LocalDate.of(2020, 1, 1));
        request.setLevelId(barberLevelId);
        BarberResponse barber = barberService.createBarber(merchantId, request);

        assertEquals(0, barber.getOrderCount());

        // 手动增加订单数（模拟完成订单）
        Barber barberEntity = barberRepository.findById(barber.getId()).get();
        barberEntity.setOrderCount(5);
        barberRepository.save(barberEntity);

        BarberResponse updated = barberService.getBarber(merchantId, barber.getId());
        assertEquals(5, updated.getOrderCount());
    }

    @Test
    void testBarberRatingCalculation() {
        BarberCreateRequest request = new BarberCreateRequest();
        request.setName("王师傅");
        request.setPhone("13600136000");
        request.setGender("MALE");
        request.setEntryDate(LocalDate.of(2020, 1, 1));
        request.setLevelId(barberLevelId);
        BarberResponse barber = barberService.createBarber(merchantId, request);

        // 默认评分应该是5.0
        assertEquals(5.0, barber.getRating());

        // 更新评分
        Barber barberEntity = barberRepository.findById(barber.getId()).get();
        barberEntity.setTotalRatingPoints(45);
        barberEntity.setRatingCount(10);
        barberRepository.save(barberEntity);

        BarberResponse updated = barberService.getBarber(merchantId, barber.getId());
        assertEquals(4.5, updated.getRating());
    }
}
