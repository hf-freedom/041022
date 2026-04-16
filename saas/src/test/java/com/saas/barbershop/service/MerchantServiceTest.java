package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.Merchant;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class MerchantServiceTest {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MerchantRegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new MerchantRegisterRequest();
        registerRequest.setShopName("测试理发店");
        registerRequest.setOwnerName("张三");
        registerRequest.setPhone("13800138000");
        registerRequest.setPassword("123456");
        registerRequest.setEmail("test@example.com");
        registerRequest.setAddress("北京市朝阳区");
    }

    @Test
    void testRegister() {
        MerchantResponse response = merchantService.register(registerRequest);

        assertNotNull(response);
        assertEquals("测试理发店", response.getShopName());
        assertEquals("张三", response.getOwnerName());
        assertEquals("13800138000", response.getPhone());
        assertEquals(Merchant.MerchantStatus.PENDING, response.getStatus());
    }

    @Test
    void testRegisterDuplicatePhone() {
        merchantService.register(registerRequest);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            merchantService.register(registerRequest);
        });

        assertEquals("该手机号已被注册", exception.getMessage());
    }

    @Test
    void testGetCurrentMerchant() {
        MerchantResponse registered = merchantService.register(registerRequest);

        MerchantResponse found = merchantService.getCurrentMerchant(registered.getId());

        assertNotNull(found);
        assertEquals(registered.getId(), found.getId());
    }

    @Test
    void testAuditMerchant() {
        MerchantResponse registered = merchantService.register(registerRequest);

        MerchantAuditRequest auditRequest = new MerchantAuditRequest();
        auditRequest.setStatus(Merchant.MerchantStatus.APPROVED);
        auditRequest.setAuditRemark("审核通过");

        MerchantResponse audited = merchantService.auditMerchant(registered.getId(), auditRequest);

        assertEquals(Merchant.MerchantStatus.APPROVED, audited.getStatus());
        assertEquals("审核通过", audited.getAuditRemark());
    }

    @Test
    void testDisableAndEnableMerchant() {
        MerchantResponse registered = merchantService.register(registerRequest);

        MerchantAuditRequest auditRequest = new MerchantAuditRequest();
        auditRequest.setStatus(Merchant.MerchantStatus.APPROVED);
        merchantService.auditMerchant(registered.getId(), auditRequest);

        merchantService.disableMerchant(registered.getId());
        MerchantResponse disabled = merchantService.getCurrentMerchant(registered.getId());
        assertEquals(Merchant.MerchantStatus.DISABLED, disabled.getStatus());

        merchantService.enableMerchant(registered.getId());
        MerchantResponse enabled = merchantService.getCurrentMerchant(registered.getId());
        assertEquals(Merchant.MerchantStatus.APPROVED, enabled.getStatus());
    }
}
