package com.barbershop.saas;

import com.barbershop.saas.common.MerchantContext;
import com.barbershop.saas.dto.BarberDTO;
import com.barbershop.saas.entity.Barber;
import com.barbershop.saas.entity.BarberLevel;
import com.barbershop.saas.service.BarberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
public class BarberServiceTest {

    @Autowired
    private BarberService barberService;

    @BeforeEach
    void setUp() {
        MerchantContext.setMerchantId(1L);
    }

    @AfterEach
    void tearDown() {
        MerchantContext.clear();
    }

    @Test
    void testAddBarber() {
        BarberDTO dto = new BarberDTO();
        dto.setName("测试理发师" + System.currentTimeMillis());
        dto.setPhone("137" + System.currentTimeMillis() % 100000000);
        dto.setLevelId(2);
        dto.setSkillTags("剪发,烫发,染发");
        dto.setStatus(1);

        barberService.addBarber(dto);
        log.info("理发师添加成功");
    }

    @Test
    void testUpdateBarber() {
        List<Barber> list = barberService.list((String) null);
        if (!list.isEmpty()) {
            Barber barber = list.get(0);
            BarberDTO dto = new BarberDTO();
            dto.setName(barber.getName() + "-更新");
            dto.setPhone(barber.getPhone());
            dto.setLevelId(3);
            dto.setSkillTags("剪发,烫发,染发,精修");
            dto.setStatus(1);

            barberService.updateBarber(barber.getId(), dto);
            Barber updated = barberService.getById(barber.getId());
            log.info("理发师更新后: {}, 级别: {}", updated.getName(), updated.getLevelName());
        }
    }

    @Test
    void testMatchBarbers() {
        List<String> details = Arrays.asList("染发", "烫发");
        List<Barber> matched = barberService.matchBarbers(1L, details, "染发");
        log.info("匹配到理发师数量: {}", matched.size());
        matched.forEach(b -> log.info("理发师: {}, 技能: {}, 提成比例: {}",
                b.getName(), b.getSkillTags(), b.getCommissionRate()));
    }

    @Test
    void testBarberLevels() {
        List<BarberLevel> levels = barberService.listLevels();
        log.info("理发师级别数量: {}", levels.size());
        levels.forEach(level -> log.info("级别: {}, 提成比例: {}",
                level.getName(), level.getCommissionRate()));
        Assert.notEmpty(levels, "理发师级别不能为空");
    }

    @Test
    void testListBarbers() {
        List<Barber> list = barberService.list((String) null);
        log.info("理发师数量: {}", list.size());
        list.forEach(b -> log.info("理发师: {}, 级别: {}, 接单: {}, 总提成: {}",
                b.getName(), b.getLevelName(), b.getOrderCount(), b.getTotalCommission()));
    }
}
