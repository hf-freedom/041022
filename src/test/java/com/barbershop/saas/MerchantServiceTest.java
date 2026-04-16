package com.barbershop.saas;

import com.barbershop.saas.common.JwtUtil;
import com.barbershop.saas.dto.MerchantLoginDTO;
import com.barbershop.saas.dto.MerchantRegisterDTO;
import com.barbershop.saas.entity.Merchant;
import com.barbershop.saas.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class MerchantServiceTest {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testRegister() {
        MerchantRegisterDTO dto = new MerchantRegisterDTO();
        dto.setShopName("测试理发店" + System.currentTimeMillis());
        dto.setContactName("测试店长");
        dto.setPhone("138" + System.currentTimeMillis() % 100000000);
        dto.setAddress("测试地址");
        dto.setUsername("testuser" + System.currentTimeMillis());
        dto.setPassword("123456");

        merchantService.register(dto);
        log.info("商家注册成功");
    }

    @Test
    void testLogin() {
        MerchantLoginDTO dto = new MerchantLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin");

        Map<String, Object> result = merchantService.login(dto);
        log.info("登录结果: {}", result);
        Assert.notNull(result.get("token"), "token不能为空");
    }

    @Test
    void testJwtGenerate() {
        String token = jwtUtil.generateToken(1L, "admin");
        log.info("生成token: {}", token);
        Assert.isTrue(jwtUtil.validateToken(token), "token验证失败");

        Long merchantId = jwtUtil.getMerchantIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        log.info("解析token: merchantId={}, username={}", merchantId, username);
        Assert.isTrue(merchantId == 1L, "商家ID解析错误");
        Assert.isTrue(username.equals("admin"), "用户名解析错误");
    }

    @Test
    void testAudit() {
        List<Merchant> list = merchantService.listForAdmin(0);
        if (!list.isEmpty()) {
            Merchant merchant = list.get(0);
            merchantService.audit(merchant.getId(), 1, "审核通过");
            log.info("审核商家: {}", merchant.getId());
        }
    }

    @Test
    void testToggleOnline() {
        List<Merchant> list = merchantService.listForAdmin(1);
        if (!list.isEmpty()) {
            Merchant merchant = list.get(0);
            merchantService.toggleOnline(merchant.getId(), 1);
            log.info("商家上线: {}", merchant.getId());
        }
    }
}
