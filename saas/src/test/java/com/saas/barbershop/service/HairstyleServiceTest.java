package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.HairstyleType;
import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class HairstyleServiceTest {

    @Autowired
    private HairstyleService hairstyleService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantRepository merchantRepository;

    private Long merchantId;

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
    }

    @Test
    void testCreateHairstyleType() {
        HairstyleTypeRequest request = new HairstyleTypeRequest();
        request.setName("男士剪发");
        request.setCategory("CUT");
        request.setBasePrice(new BigDecimal("50"));
        request.setDurationMinutes(30);
        request.setDescription("基础男士剪发服务");

        HairstyleTypeResponse response = hairstyleService.createHairstyleType(merchantId, request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("男士剪发", response.getName());
        assertEquals(HairstyleType.HairstyleCategory.CUT, response.getCategory());
        assertEquals(new BigDecimal("50.00"), response.getBasePrice());
        assertEquals(30, response.getDurationMinutes());
    }

    @Test
    void testCreateHairstyleTypeWithAllCategories() {
        // 剪发
        HairstyleTypeRequest cutRequest = new HairstyleTypeRequest();
        cutRequest.setName("精剪");
        cutRequest.setCategory("CUT");
        cutRequest.setBasePrice(new BigDecimal("50"));
        cutRequest.setDurationMinutes(30);
        HairstyleTypeResponse cut = hairstyleService.createHairstyleType(merchantId, cutRequest);
        assertEquals(HairstyleType.HairstyleCategory.CUT, cut.getCategory());

        // 烫发
        HairstyleTypeRequest permRequest = new HairstyleTypeRequest();
        permRequest.setName("纹理烫");
        permRequest.setCategory("PERM");
        permRequest.setBasePrice(new BigDecimal("200"));
        permRequest.setDurationMinutes(120);
        HairstyleTypeResponse perm = hairstyleService.createHairstyleType(merchantId, permRequest);
        assertEquals(HairstyleType.HairstyleCategory.PERM, perm.getCategory());

        // 染发
        HairstyleTypeRequest colorRequest = new HairstyleTypeRequest();
        colorRequest.setName("染发");
        colorRequest.setCategory("COLOR");
        colorRequest.setBasePrice(new BigDecimal("150"));
        colorRequest.setDurationMinutes(90);
        HairstyleTypeResponse color = hairstyleService.createHairstyleType(merchantId, colorRequest);
        assertEquals(HairstyleType.HairstyleCategory.COLOR, color.getCategory());

        // 护理
        HairstyleTypeRequest careRequest = new HairstyleTypeRequest();
        careRequest.setName("头皮护理");
        careRequest.setCategory("CARE");
        careRequest.setBasePrice(new BigDecimal("80"));
        careRequest.setDurationMinutes(45);
        HairstyleTypeResponse care = hairstyleService.createHairstyleType(merchantId, careRequest);
        assertEquals(HairstyleType.HairstyleCategory.CARE, care.getCategory());

        // 造型
        HairstyleTypeRequest stylingRequest = new HairstyleTypeRequest();
        stylingRequest.setName("造型设计");
        stylingRequest.setCategory("STYLING");
        stylingRequest.setBasePrice(new BigDecimal("100"));
        stylingRequest.setDurationMinutes(60);
        HairstyleTypeResponse styling = hairstyleService.createHairstyleType(merchantId, stylingRequest);
        assertEquals(HairstyleType.HairstyleCategory.STYLING, styling.getCategory());
    }

    @Test
    void testGetHairstyleTypeById() {
        HairstyleTypeRequest request = new HairstyleTypeRequest();
        request.setName("男士剪发");
        request.setCategory("CUT");
        request.setBasePrice(new BigDecimal("50"));
        request.setDurationMinutes(30);
        HairstyleTypeResponse created = hairstyleService.createHairstyleType(merchantId, request);

        HairstyleTypeResponse retrieved = hairstyleService.getHairstyleType(merchantId, created.getId());

        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals("男士剪发", retrieved.getName());
    }

    @Test
    void testGetHairstyleTypeNotFound() {
        assertThrows(BusinessException.class, () -> {
            hairstyleService.getHairstyleType(merchantId, 99999L);
        });
    }

    @Test
    void testGetAllHairstyleTypes() {
        // 创建多个发型类型
        for (int i = 0; i < 3; i++) {
            HairstyleTypeRequest request = new HairstyleTypeRequest();
            request.setName("发型" + i);
            request.setCategory(i % 2 == 0 ? "CUT" : "PERM");
            request.setBasePrice(new BigDecimal(50 + i * 10));
            request.setDurationMinutes(30 + i * 10);
            hairstyleService.createHairstyleType(merchantId, request);
        }

        List<HairstyleTypeResponse> types = hairstyleService.getAllHairstyleTypes(merchantId);

        assertEquals(3, types.size());
    }

    @Test
    void testGetHairstyleTypesByCategory() {
        // 创建剪发类型
        HairstyleTypeRequest cutRequest = new HairstyleTypeRequest();
        cutRequest.setName("精剪");
        cutRequest.setCategory("CUT");
        cutRequest.setBasePrice(new BigDecimal("50"));
        cutRequest.setDurationMinutes(30);
        hairstyleService.createHairstyleType(merchantId, cutRequest);

        HairstyleTypeRequest cutRequest2 = new HairstyleTypeRequest();
        cutRequest2.setName("快剪");
        cutRequest2.setCategory("CUT");
        cutRequest2.setBasePrice(new BigDecimal("30"));
        cutRequest2.setDurationMinutes(15);
        hairstyleService.createHairstyleType(merchantId, cutRequest2);

        // 创建烫发类型
        HairstyleTypeRequest permRequest = new HairstyleTypeRequest();
        permRequest.setName("纹理烫");
        permRequest.setCategory("PERM");
        permRequest.setBasePrice(new BigDecimal("200"));
        permRequest.setDurationMinutes(120);
        hairstyleService.createHairstyleType(merchantId, permRequest);

        List<HairstyleTypeResponse> cutTypes = hairstyleService.getHairstyleTypesByCategory(merchantId, HairstyleType.HairstyleCategory.CUT);
        assertEquals(2, cutTypes.size());

        List<HairstyleTypeResponse> permTypes = hairstyleService.getHairstyleTypesByCategory(merchantId, HairstyleType.HairstyleCategory.PERM);
        assertEquals(1, permTypes.size());
    }

    @Test
    void testUpdateHairstyleType() {
        HairstyleTypeRequest createRequest = new HairstyleTypeRequest();
        createRequest.setName("男士剪发");
        createRequest.setCategory("CUT");
        createRequest.setBasePrice(new BigDecimal("50"));
        createRequest.setDurationMinutes(30);
        HairstyleTypeResponse created = hairstyleService.createHairstyleType(merchantId, createRequest);

        HairstyleTypeRequest updateRequest = new HairstyleTypeRequest();
        updateRequest.setName("男士精剪");
        updateRequest.setCategory("CUT");
        updateRequest.setBasePrice(new BigDecimal("60"));
        updateRequest.setDurationMinutes(40);
        updateRequest.setDescription("升级服务");

        HairstyleTypeResponse updated = hairstyleService.updateHairstyleType(merchantId, created.getId(), updateRequest);

        assertEquals("男士精剪", updated.getName());
        assertEquals(new BigDecimal("60.00"), updated.getBasePrice());
        assertEquals(40, updated.getDurationMinutes());
    }

    @Test
    void testDeleteHairstyleType() {
        HairstyleTypeRequest request = new HairstyleTypeRequest();
        request.setName("男士剪发");
        request.setCategory("CUT");
        request.setBasePrice(new BigDecimal("50"));
        request.setDurationMinutes(30);
        HairstyleTypeResponse created = hairstyleService.createHairstyleType(merchantId, request);

        hairstyleService.deleteHairstyleType(merchantId, created.getId());

        assertThrows(BusinessException.class, () -> {
            hairstyleService.getHairstyleType(merchantId, created.getId());
        });
    }

    @Test
    void testCreateHairstyleDetail() {
        // 先创建发型类型
        HairstyleTypeRequest typeRequest = new HairstyleTypeRequest();
        typeRequest.setName("男士剪发");
        typeRequest.setCategory("CUT");
        typeRequest.setBasePrice(new BigDecimal("50"));
        typeRequest.setDurationMinutes(30);
        HairstyleTypeResponse type = hairstyleService.createHairstyleType(merchantId, typeRequest);

        // 创建发型细节
        HairstyleDetailRequest detailRequest = new HairstyleDetailRequest();
        detailRequest.setName("精剪");
        detailRequest.setAdditionalPrice(new BigDecimal("20"));
        detailRequest.setAdditionalMinutes(10);
        detailRequest.setDescription("精细修剪");

        HairstyleDetailResponse response = hairstyleService.createHairstyleDetail(merchantId, type.getId(), detailRequest);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("精剪", response.getName());
        assertEquals(new BigDecimal("20.00"), response.getAdditionalPrice());
        assertEquals(10, response.getAdditionalMinutes());
    }

    @Test
    void testCreateMultipleHairstyleDetails() {
        // 创建发型类型
        HairstyleTypeRequest typeRequest = new HairstyleTypeRequest();
        typeRequest.setName("男士剪发");
        typeRequest.setCategory("CUT");
        typeRequest.setBasePrice(new BigDecimal("50"));
        typeRequest.setDurationMinutes(30);
        HairstyleTypeResponse type = hairstyleService.createHairstyleType(merchantId, typeRequest);

        // 创建多个细节
        String[] detailNames = {"精剪", "洗头", "按摩", "造型"};
        BigDecimal[] prices = {new BigDecimal("20"), new BigDecimal("10"), new BigDecimal("15"), new BigDecimal("25")};

        for (int i = 0; i < detailNames.length; i++) {
            HairstyleDetailRequest detailRequest = new HairstyleDetailRequest();
            detailRequest.setName(detailNames[i]);
            detailRequest.setAdditionalPrice(prices[i]);
            detailRequest.setAdditionalMinutes(10);
            hairstyleService.createHairstyleDetail(merchantId, type.getId(), detailRequest);
        }

        List<HairstyleDetailResponse> details = hairstyleService.getHairstyleDetails(merchantId, type.getId());
        assertEquals(4, details.size());
    }

    @Test
    void testGetHairstyleDetailById() {
        HairstyleTypeRequest typeRequest = new HairstyleTypeRequest();
        typeRequest.setName("男士剪发");
        typeRequest.setCategory("CUT");
        typeRequest.setBasePrice(new BigDecimal("50"));
        HairstyleTypeResponse type = hairstyleService.createHairstyleType(merchantId, typeRequest);

        HairstyleDetailRequest detailRequest = new HairstyleDetailRequest();
        detailRequest.setName("精剪");
        detailRequest.setAdditionalPrice(new BigDecimal("20"));
        HairstyleDetailResponse created = hairstyleService.createHairstyleDetail(merchantId, type.getId(), detailRequest);

        HairstyleDetailResponse retrieved = hairstyleService.getHairstyleDetail(merchantId, type.getId(), created.getId());

        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals("精剪", retrieved.getName());
    }

    @Test
    void testUpdateHairstyleDetail() {
        HairstyleTypeRequest typeRequest = new HairstyleTypeRequest();
        typeRequest.setName("男士剪发");
        typeRequest.setCategory("CUT");
        typeRequest.setBasePrice(new BigDecimal("50"));
        HairstyleTypeResponse type = hairstyleService.createHairstyleType(merchantId, typeRequest);

        HairstyleDetailRequest createRequest = new HairstyleDetailRequest();
        createRequest.setName("精剪");
        createRequest.setAdditionalPrice(new BigDecimal("20"));
        createRequest.setAdditionalMinutes(10);
        HairstyleDetailResponse created = hairstyleService.createHairstyleDetail(merchantId, type.getId(), createRequest);

        HairstyleDetailRequest updateRequest = new HairstyleDetailRequest();
        updateRequest.setName("超精剪");
        updateRequest.setAdditionalPrice(new BigDecimal("30"));
        updateRequest.setAdditionalMinutes(15);
        updateRequest.setDescription("升级版");

        HairstyleDetailResponse updated = hairstyleService.updateHairstyleDetail(merchantId, type.getId(), created.getId(), updateRequest);

        assertEquals("超精剪", updated.getName());
        assertEquals(new BigDecimal("30.00"), updated.getAdditionalPrice());
        assertEquals(15, updated.getAdditionalMinutes());
    }

    @Test
    void testDeleteHairstyleDetail() {
        HairstyleTypeRequest typeRequest = new HairstyleTypeRequest();
        typeRequest.setName("男士剪发");
        typeRequest.setCategory("CUT");
        typeRequest.setBasePrice(new BigDecimal("50"));
        HairstyleTypeResponse type = hairstyleService.createHairstyleType(merchantId, typeRequest);

        HairstyleDetailRequest detailRequest = new HairstyleDetailRequest();
        detailRequest.setName("精剪");
        detailRequest.setAdditionalPrice(new BigDecimal("20"));
        HairstyleDetailResponse created = hairstyleService.createHairstyleDetail(merchantId, type.getId(), detailRequest);

        hairstyleService.deleteHairstyleDetail(merchantId, type.getId(), created.getId());

        assertThrows(BusinessException.class, () -> {
            hairstyleService.getHairstyleDetail(merchantId, type.getId(), created.getId());
        });
    }

    @Test
    void testCreateHairstyleDetailWithZeroPrice() {
        HairstyleTypeRequest typeRequest = new HairstyleTypeRequest();
        typeRequest.setName("男士剪发");
        typeRequest.setCategory("CUT");
        typeRequest.setBasePrice(new BigDecimal("50"));
        HairstyleTypeResponse type = hairstyleService.createHairstyleType(merchantId, typeRequest);

        HairstyleDetailRequest detailRequest = new HairstyleDetailRequest();
        detailRequest.setName("基础护理");
        detailRequest.setAdditionalPrice(BigDecimal.ZERO);
        detailRequest.setAdditionalMinutes(5);
        detailRequest.setDescription("免费护理");

        HairstyleDetailResponse response = hairstyleService.createHairstyleDetail(merchantId, type.getId(), detailRequest);

        assertEquals(BigDecimal.ZERO, response.getAdditionalPrice());
    }

    @Test
    void testCreateHairstyleTypeWithZeroDuration() {
        HairstyleTypeRequest request = new HairstyleTypeRequest();
        request.setName("咨询服务");
        request.setCategory("CARE");
        request.setBasePrice(new BigDecimal("0"));
        request.setDurationMinutes(0);
        request.setDescription("免费咨询");

        HairstyleTypeResponse response = hairstyleService.createHairstyleType(merchantId, request);

        assertEquals(BigDecimal.ZERO, response.getBasePrice());
        assertEquals(0, response.getDurationMinutes());
    }

    @Test
    void testGetHairstyleDetailsForNonExistentType() {
        assertThrows(BusinessException.class, () -> {
            hairstyleService.getHairstyleDetails(merchantId, 99999L);
        });
    }

    @Test
    void testCreateHairstyleDetailForNonExistentType() {
        HairstyleDetailRequest detailRequest = new HairstyleDetailRequest();
        detailRequest.setName("精剪");
        detailRequest.setAdditionalPrice(new BigDecimal("20"));

        assertThrows(BusinessException.class, () -> {
            hairstyleService.createHairstyleDetail(merchantId, 99999L, detailRequest);
        });
    }
}
