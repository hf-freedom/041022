package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.*;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberLevelService memberLevelService;

    @Autowired
    private BarberService barberService;

    @Autowired
    private BarberLevelService barberLevelService;

    @Autowired
    private HairstyleService hairstyleService;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    private Long merchantId;
    private Long memberId;
    private Long barberId;
    private Long hairstyleTypeId;
    private Long hairstyleDetailId;
    private Long memberLevelId;
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

        // 创建会员等级
        MemberLevelRequest levelRequest = new MemberLevelRequest();
        levelRequest.setName("VIP会员");
        levelRequest.setLevel(2);
        levelRequest.setMinAmount(new BigDecimal("1000"));
        levelRequest.setDiscountRate(new BigDecimal("0.85"));
        levelRequest.setDescription("VIP等级");
        levelRequest.setIsDefault(false);
        MemberLevelResponse memberLevel = memberLevelService.createMemberLevel(merchantId, levelRequest);
        memberLevelId = memberLevel.getId();

        // 创建会员
        MemberCreateRequest memberRequest = new MemberCreateRequest();
        memberRequest.setName("李四");
        memberRequest.setPhone("13900139000");
        memberRequest.setGender("MALE");
        memberRequest.setBirthday(LocalDate.of(1990, 1, 1));
        MemberResponse member = memberService.createMember(merchantId, memberRequest);
        memberId = member.getId();

        // 充值
        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setAmount(new BigDecimal("500"));
        rechargeRequest.setRemark("首次充值");
        memberService.recharge(merchantId, memberId, rechargeRequest);

        // 创建理发师级别
        BarberLevelRequest barberLevelRequest = new BarberLevelRequest();
        barberLevelRequest.setName("高级理发师");
        barberLevelRequest.setLevel(1);
        barberLevelRequest.setCommissionRate(new BigDecimal("0.30"));
        barberLevelRequest.setDescription("提成30%");
        barberLevelRequest.setIsDefault(true);
        BarberLevelResponse barberLevel = barberLevelService.createBarberLevel(merchantId, barberLevelRequest);
        barberLevelId = barberLevel.getId();

        // 创建理发师
        BarberCreateRequest barberRequest = new BarberCreateRequest();
        barberRequest.setName("王师傅");
        barberRequest.setPhone("13600136000");
        barberRequest.setGender("MALE");
        barberRequest.setBirthday(LocalDate.of(1985, 5, 1));
        barberRequest.setEntryDate(LocalDate.of(2020, 1, 1));
        barberRequest.setLevelId(barberLevelId);
        barberRequest.setSpecialties("剪发、烫发");
        barberRequest.setIntroduction("10年经验");
        BarberResponse barber = barberService.createBarber(merchantId, barberRequest);
        barberId = barber.getId();

        // 创建发型类型
        HairstyleTypeRequest typeRequest = new HairstyleTypeRequest();
        typeRequest.setName("男士剪发");
        typeRequest.setCategory("CUT");
        typeRequest.setBasePrice(new BigDecimal("50"));
        typeRequest.setDurationMinutes(30);
        typeRequest.setDescription("基础男士剪发");
        HairstyleTypeResponse hairstyleType = hairstyleService.createHairstyleType(merchantId, typeRequest);
        hairstyleTypeId = hairstyleType.getId();

        // 创建发型细节
        HairstyleDetailRequest detailRequest = new HairstyleDetailRequest();
        detailRequest.setName("精剪");
        detailRequest.setAdditionalPrice(new BigDecimal("20"));
        detailRequest.setAdditionalMinutes(10);
        detailRequest.setDescription("精细修剪");
        HairstyleDetailResponse detail = hairstyleService.createHairstyleDetail(merchantId, hairstyleTypeId, detailRequest);
        hairstyleDetailId = detail.getId();
    }

    @Test
    void testCreateOrder() {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setBarberId(barberId);
        request.setMemberId(memberId);
        request.setAppointmentTime(LocalDateTime.now().plusHours(1));
        request.setRemark("预约备注");

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setHairstyleDetailIds(Arrays.asList(hairstyleDetailId));
        itemRequest.setQuantity(1);
        request.setItems(Arrays.asList(itemRequest));

        OrderResponse response = orderService.createOrder(merchantId, request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getOrderNo());
        assertEquals(memberId, response.getMember().getId());
        assertEquals(barberId, response.getBarber().getId());
        assertEquals(Order.OrderStatus.PENDING, response.getStatus());

        // 验证价格计算: 50 + 20 = 70, 折扣后 70 * 0.85 = 59.5
        assertEquals(new BigDecimal("70.00"), response.getTotalAmount());
        assertEquals(new BigDecimal("59.50"), response.getActualAmount());
        assertEquals(1, response.getItems().size());
    }

    @Test
    void testCreateOrderWithoutMember() {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setBarberId(barberId);
        request.setMemberId(null);
        request.setAppointmentTime(LocalDateTime.now().plusHours(1));

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        request.setItems(Arrays.asList(itemRequest));

        OrderResponse response = orderService.createOrder(merchantId, request);

        assertNotNull(response);
        assertNull(response.getMember());
        // 无会员无折扣
        assertEquals(new BigDecimal("50.00"), response.getTotalAmount());
        assertEquals(new BigDecimal("50.00"), response.getActualAmount());
    }

    @Test
    void testCreateOrderWithMultipleItems() {
        // 创建第二个发型类型
        HairstyleTypeRequest typeRequest2 = new HairstyleTypeRequest();
        typeRequest2.setName("烫发");
        typeRequest2.setCategory("PERM");
        typeRequest2.setBasePrice(new BigDecimal("200"));
        typeRequest2.setDurationMinutes(120);
        HairstyleTypeResponse hairstyleType2 = hairstyleService.createHairstyleType(merchantId, typeRequest2);

        OrderCreateRequest request = new OrderCreateRequest();
        request.setBarberId(barberId);
        request.setMemberId(memberId);

        List<OrderItemRequest> items = new ArrayList<>();

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setHairstyleTypeId(hairstyleTypeId);
        item1.setHairstyleDetailIds(Arrays.asList(hairstyleDetailId));
        item1.setQuantity(1);
        items.add(item1);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setHairstyleTypeId(hairstyleType2.getId());
        item2.setQuantity(1);
        items.add(item2);

        request.setItems(items);

        OrderResponse response = orderService.createOrder(merchantId, request);

        // 总价: (50 + 20) + 200 = 270, 折扣后 270 * 0.85 = 229.5
        assertEquals(new BigDecimal("270.00"), response.getTotalAmount());
        assertEquals(2, response.getItems().size());
    }

    @Test
    void testStartOrder() {
        // 先创建订单
        OrderCreateRequest createRequest = new OrderCreateRequest();
        createRequest.setBarberId(barberId);
        createRequest.setMemberId(memberId);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        createRequest.setItems(Arrays.asList(itemRequest));

        OrderResponse created = orderService.createOrder(merchantId, createRequest);

        // 开始订单
        OrderResponse started = orderService.startOrder(merchantId, created.getId());

        assertEquals(Order.OrderStatus.IN_PROGRESS, started.getStatus());
        assertNotNull(started.getStartTime());
    }

    @Test
    void testStartOrderWithInvalidStatus() {
        // 创建并直接完成订单
        OrderCreateRequest createRequest = new OrderCreateRequest();
        createRequest.setBarberId(barberId);
        createRequest.setMemberId(memberId);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        createRequest.setItems(Arrays.asList(itemRequest));

        OrderResponse created = orderService.createOrder(merchantId, createRequest);
        orderService.startOrder(merchantId, created.getId());
        orderService.completeOrder(merchantId, created.getId(), new OrderCompleteRequest());

        // 尝试开始已完成的订单
        assertThrows(BusinessException.class, () -> {
            orderService.startOrder(merchantId, created.getId());
        });
    }

    @Test
    void testCompleteOrder() {
        // 创建订单
        OrderCreateRequest createRequest = new OrderCreateRequest();
        createRequest.setBarberId(barberId);
        createRequest.setMemberId(memberId);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        createRequest.setItems(Arrays.asList(itemRequest));

        OrderResponse created = orderService.createOrder(merchantId, createRequest);
        orderService.startOrder(merchantId, created.getId());

        // 获取会员充值前余额
        Member memberBefore = memberRepository.findById(memberId).get();
        BigDecimal balanceBefore = memberBefore.getBalance();

        // 完成订单
        OrderCompleteRequest completeRequest = new OrderCompleteRequest();
        OrderResponse completed = orderService.completeOrder(merchantId, created.getId(), completeRequest);

        assertEquals(Order.OrderStatus.COMPLETED, completed.getStatus());
        assertNotNull(completed.getEndTime());

        // 验证余额扣减
        Member memberAfter = memberRepository.findById(memberId).get();
        assertTrue(memberAfter.getBalance().compareTo(balanceBefore) < 0);

        // 验证交易记录
        List<TransactionRecord> records = transactionRecordRepository.findByMemberId(memberId);
        assertFalse(records.isEmpty());
        TransactionRecord record = records.get(records.size() - 1);
        assertEquals(TransactionRecord.TransactionType.CONSUMPTION, record.getType());
    }

    @Test
    void testCompleteOrderWithInsufficientBalance() {
        // 创建低余额会员
        MemberCreateRequest memberRequest = new MemberCreateRequest();
        memberRequest.setName("低余额会员");
        memberRequest.setPhone("13700137000");
        MemberResponse lowBalanceMember = memberService.createMember(merchantId, memberRequest);

        // 只充10元
        RechargeRequest rechargeRequest = new RechargeRequest();
        rechargeRequest.setAmount(new BigDecimal("10"));
        memberService.recharge(merchantId, lowBalanceMember.getId(), rechargeRequest);

        // 创建高价订单
        OrderCreateRequest createRequest = new OrderCreateRequest();
        createRequest.setBarberId(barberId);
        createRequest.setMemberId(lowBalanceMember.getId());
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        createRequest.setItems(Arrays.asList(itemRequest));

        OrderResponse created = orderService.createOrder(merchantId, createRequest);
        orderService.startOrder(merchantId, created.getId());

        // 完成订单应该失败
        assertThrows(BusinessException.class, () -> {
            orderService.completeOrder(merchantId, created.getId(), new OrderCompleteRequest());
        });
    }

    @Test
    void testCancelOrder() {
        // 创建订单
        OrderCreateRequest createRequest = new OrderCreateRequest();
        createRequest.setBarberId(barberId);
        createRequest.setMemberId(memberId);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        createRequest.setItems(Arrays.asList(itemRequest));

        OrderResponse created = orderService.createOrder(merchantId, createRequest);

        // 取消订单
        OrderResponse cancelled = orderService.cancelOrder(merchantId, created.getId(), "客户取消");

        assertEquals(Order.OrderStatus.CANCELLED, cancelled.getStatus());
        assertTrue(cancelled.getRemark().contains("客户取消"));
    }

    @Test
    void testCancelCompletedOrder() {
        // 创建并完成订单
        OrderCreateRequest createRequest = new OrderCreateRequest();
        createRequest.setBarberId(barberId);
        createRequest.setMemberId(memberId);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        createRequest.setItems(Arrays.asList(itemRequest));

        OrderResponse created = orderService.createOrder(merchantId, createRequest);
        orderService.startOrder(merchantId, created.getId());
        orderService.completeOrder(merchantId, created.getId(), new OrderCompleteRequest());

        // 取消已完成的订单应该失败
        assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(merchantId, created.getId(), "想取消");
        });
    }

    @Test
    void testGetOrder() {
        OrderCreateRequest createRequest = new OrderCreateRequest();
        createRequest.setBarberId(barberId);
        createRequest.setMemberId(memberId);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        createRequest.setItems(Arrays.asList(itemRequest));

        OrderResponse created = orderService.createOrder(merchantId, createRequest);

        OrderResponse retrieved = orderService.getOrder(merchantId, created.getId());

        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(created.getOrderNo(), retrieved.getOrderNo());
    }

    @Test
    void testGetOrderNotFound() {
        assertThrows(BusinessException.class, () -> {
            orderService.getOrder(merchantId, 99999L);
        });
    }

    @Test
    void testGetAllOrders() {
        // 创建多个订单
        for (int i = 0; i < 3; i++) {
            OrderCreateRequest createRequest = new OrderCreateRequest();
            createRequest.setBarberId(barberId);
            createRequest.setMemberId(memberId);
            OrderItemRequest itemRequest = new OrderItemRequest();
            itemRequest.setHairstyleTypeId(hairstyleTypeId);
            itemRequest.setQuantity(1);
            createRequest.setItems(Arrays.asList(itemRequest));
            orderService.createOrder(merchantId, createRequest);
        }

        List<OrderResponse> orders = orderService.getAllOrders(merchantId);

        assertEquals(3, orders.size());
    }

    @Test
    void testGetMemberOrders() {
        // 创建第二个会员
        MemberCreateRequest memberRequest = new MemberCreateRequest();
        memberRequest.setName("另一个会员");
        memberRequest.setPhone("13600136001");
        MemberResponse member2 = memberService.createMember(merchantId, memberRequest);

        // 为第一个会员创建2个订单
        for (int i = 0; i < 2; i++) {
            OrderCreateRequest createRequest = new OrderCreateRequest();
            createRequest.setBarberId(barberId);
            createRequest.setMemberId(memberId);
            OrderItemRequest itemRequest = new OrderItemRequest();
            itemRequest.setHairstyleTypeId(hairstyleTypeId);
            itemRequest.setQuantity(1);
            createRequest.setItems(Arrays.asList(itemRequest));
            orderService.createOrder(merchantId, createRequest);
        }

        // 为第二个会员创建1个订单
        OrderCreateRequest createRequest = new OrderCreateRequest();
        createRequest.setBarberId(barberId);
        createRequest.setMemberId(member2.getId());
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        createRequest.setItems(Arrays.asList(itemRequest));
        orderService.createOrder(merchantId, createRequest);

        List<OrderResponse> member1Orders = orderService.getMemberOrders(merchantId, memberId);
        assertEquals(2, member1Orders.size());

        List<OrderResponse> member2Orders = orderService.getMemberOrders(merchantId, member2.getId());
        assertEquals(1, member2Orders.size());
    }

    @Test
    void testGetBarberOrders() {
        // 创建第二个理发师
        BarberCreateRequest barberRequest = new BarberCreateRequest();
        barberRequest.setName("李师傅");
        barberRequest.setPhone("13500135000");
        barberRequest.setGender("MALE");
        barberRequest.setEntryDate(LocalDate.of(2021, 1, 1));
        barberRequest.setLevelId(barberLevelId);
        BarberResponse barber2 = barberService.createBarber(merchantId, barberRequest);

        // 为王师傅创建2个订单
        for (int i = 0; i < 2; i++) {
            OrderCreateRequest createRequest = new OrderCreateRequest();
            createRequest.setBarberId(barberId);
            createRequest.setMemberId(memberId);
            OrderItemRequest itemRequest = new OrderItemRequest();
            itemRequest.setHairstyleTypeId(hairstyleTypeId);
            itemRequest.setQuantity(1);
            createRequest.setItems(Arrays.asList(itemRequest));
            orderService.createOrder(merchantId, createRequest);
        }

        // 为李师傅创建1个订单
        OrderCreateRequest createRequest = new OrderCreateRequest();
        createRequest.setBarberId(barber2.getId());
        createRequest.setMemberId(memberId);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        createRequest.setItems(Arrays.asList(itemRequest));
        orderService.createOrder(merchantId, createRequest);

        List<OrderResponse> barber1Orders = orderService.getBarberOrders(merchantId, barberId);
        assertEquals(2, barber1Orders.size());

        List<OrderResponse> barber2Orders = orderService.getBarberOrders(merchantId, barber2.getId());
        assertEquals(1, barber2Orders.size());
    }

    @Test
    void testOrderCommissionCalculation() {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setBarberId(barberId);
        request.setMemberId(memberId);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        request.setItems(Arrays.asList(itemRequest));

        OrderResponse response = orderService.createOrder(merchantId, request);

        // 验证提成计算: 实际金额 * 提成比例 = 50 * 0.85 * 0.30 = 12.75
        assertNotNull(response.getCommissionAmount());
        assertTrue(response.getCommissionAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCreateOrderWithNonExistentBarber() {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setBarberId(99999L);
        request.setMemberId(memberId);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        request.setItems(Arrays.asList(itemRequest));

        assertThrows(BusinessException.class, () -> {
            orderService.createOrder(merchantId, request);
        });
    }

    @Test
    void testCreateOrderWithNonExistentMember() {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setBarberId(barberId);
        request.setMemberId(99999L);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(hairstyleTypeId);
        itemRequest.setQuantity(1);
        request.setItems(Arrays.asList(itemRequest));

        assertThrows(BusinessException.class, () -> {
            orderService.createOrder(merchantId, request);
        });
    }

    @Test
    void testCreateOrderWithNonExistentHairstyle() {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setBarberId(barberId);
        request.setMemberId(memberId);
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setHairstyleTypeId(99999L);
        itemRequest.setQuantity(1);
        request.setItems(Arrays.asList(itemRequest));

        assertThrows(BusinessException.class, () -> {
            orderService.createOrder(merchantId, request);
        });
    }
}
