package com.barbershop.saas;

import com.barbershop.saas.common.MerchantContext;
import com.barbershop.saas.dto.HairstyleDTO;
import com.barbershop.saas.entity.Hairstyle;
import com.barbershop.saas.service.HairstyleService;
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
public class HairstyleServiceTest {

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
    void testAddHairstyle() {
        HairstyleDTO dto = new HairstyleDTO();
        dto.setName("测试发型" + System.currentTimeMillis());
        dto.setCategory("短发");
        dto.setDescription("测试发型描述");
        dto.setPrice(new BigDecimal("68"));
        dto.setDuration(30);
        dto.setSuitableGender("男女通用");
        dto.setDetailOptions("刘海,鬓角,后脑勺");
        dto.setSkillTags("剪发,精修");
        dto.setStatus(1);

        hairstyleService.addHairstyle(dto);
        log.info("发型添加成功");
    }

    @Test
    void testUpdateHairstyle() {
        List<Hairstyle> list = hairstyleService.list((String) null, (String) null);
        if (!list.isEmpty()) {
            Hairstyle hairstyle = list.get(0);
            HairstyleDTO dto = new HairstyleDTO();
            dto.setName(hairstyle.getName() + "-更新");
            dto.setCategory(hairstyle.getCategory());
            dto.setPrice(hairstyle.getPrice().add(new BigDecimal("10")));
            dto.setStatus(1);

            hairstyleService.updateHairstyle(hairstyle.getId(), dto);
            Hairstyle updated = hairstyleService.getById(hairstyle.getId());
            log.info("发型更新后: {}, 价格: {}", updated.getName(), updated.getPrice());
        }
    }

    @Test
    void testListHairstyles() {
        List<Hairstyle> list = hairstyleService.list((String) null, (String) null);
        log.info("发型数量: {}", list.size());
        list.forEach(h -> log.info("发型: {}, 分类: {}, 价格: {}, 细节: {}",
                h.getName(), h.getCategory(), h.getPrice(), h.getDetailOptions()));
    }

    @Test
    void testGetCategories() {
        List<String> categories = hairstyleService.getAllCategories();
        log.info("发型分类: {}", categories);
        Assert.notNull(categories, "分类列表不能为null");
    }
}
