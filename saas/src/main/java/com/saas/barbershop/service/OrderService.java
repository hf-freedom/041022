package com.saas.barbershop.service;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.*;
import com.saas.barbershop.exception.BusinessException;
import com.saas.barbershop.repository.*;
import com.saas.barbershop.utils.OrderNoGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private HairstyleTypeRepository hairstyleTypeRepository;

    @Autowired
    private HairstyleDetailRepository hairstyleDetailRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Transactional
    public OrderResponse createOrder(Long merchantId, OrderCreateRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new BusinessException("商家不存在"));

        Barber barber = barberRepository.findByIdAndMerchantId(request.getBarberId(), merchantId)
                .orElseThrow(() -> new BusinessException("理发师不存在"));

        Member member = null;
        BigDecimal discountRate = BigDecimal.ONE;
        if (request.getMemberId() != null) {
            member = memberRepository.findByIdAndMerchantId(request.getMemberId(), merchantId)
                    .orElseThrow(() -> new BusinessException("会员不存在"));
            if (member.getMemberLevel() != null) {
                discountRate = member.getMemberLevel().getDiscountRate();
            }
        }

        Order order = new Order();
        order.setMerchant(merchant);
        order.setBarber(barber);
        order.setMember(member);
        order.setOrderNo(OrderNoGenerator.generateOrderNo());
        order.setAppointmentTime(request.getAppointmentTime());
        order.setRemark(request.getRemark());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {
            HairstyleType hairstyleType = hairstyleTypeRepository.findByIdAndMerchantId(itemRequest.getHairstyleTypeId(), merchantId)
                    .orElseThrow(() -> new BusinessException("发型类型不存在"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setHairstyleType(hairstyleType);
            item.setHairstyleName(hairstyleType.getName());
            item.setUnitPrice(hairstyleType.getBasePrice());
            item.setQuantity(itemRequest.getQuantity());

            BigDecimal itemSubtotal = hairstyleType.getBasePrice().multiply(new BigDecimal(itemRequest.getQuantity()));

            List<OrderItemDetail> itemDetails = new ArrayList<>();
            if (itemRequest.getHairstyleDetailIds() != null && !itemRequest.getHairstyleDetailIds().isEmpty()) {
                for (Long detailId : itemRequest.getHairstyleDetailIds()) {
                    HairstyleDetail detail = hairstyleDetailRepository.findByIdAndHairstyleTypeId(detailId, hairstyleType.getId())
                            .orElseThrow(() -> new BusinessException("发型细节不存在"));

                    OrderItemDetail itemDetail = new OrderItemDetail();
                    itemDetail.setOrderItem(item);
                    itemDetail.setHairstyleDetail(detail);
                    itemDetail.setDetailName(detail.getName());
                    itemDetail.setAdditionalPrice(detail.getAdditionalPrice());
                    itemDetails.add(itemDetail);

                    itemSubtotal = itemSubtotal.add(detail.getAdditionalPrice());
                }
            }

            item.setSubtotal(itemSubtotal);
            item.setDetails(itemDetails);
            items.add(item);

            totalAmount = totalAmount.add(itemSubtotal);
        }

        BigDecimal discountAmount = totalAmount.multiply(BigDecimal.ONE.subtract(discountRate)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualAmount = totalAmount.subtract(discountAmount);

        BigDecimal commissionRate = barber.getBarberLevel() != null ? barber.getBarberLevel().getCommissionRate() : BigDecimal.ZERO;
        BigDecimal commissionAmount = actualAmount.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);

        order.setItems(items);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setActualAmount(actualAmount);
        order.setCommissionAmount(commissionAmount);

        Order savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    @Transactional
    public OrderResponse completeOrder(Long merchantId, Long orderId, OrderCompleteRequest request) {
        Order order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (order.getStatus() != Order.OrderStatus.PENDING && order.getStatus() != Order.OrderStatus.IN_PROGRESS) {
            throw new BusinessException("订单状态不正确，无法完成");
        }

        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setEndTime(LocalDateTime.now());

        Barber barber = order.getBarber();
        barber.setOrderCount(barber.getOrderCount() + 1);
        barberRepository.save(barber);

        Member member = order.getMember();
        if (member != null) {
            BigDecimal balanceBefore = member.getBalance();
            BigDecimal actualAmount = order.getActualAmount();

            if (balanceBefore.compareTo(actualAmount) >= 0) {
                BigDecimal balanceAfter = balanceBefore.subtract(actualAmount);
                member.setBalance(balanceAfter);
                member.setTotalConsumption(member.getTotalConsumption().add(actualAmount));
                member.setLastConsumptionAt(LocalDateTime.now());
                memberRepository.save(member);

                TransactionRecord record = new TransactionRecord();
                record.setMerchant(order.getMerchant());
                record.setMember(member);
                record.setType(TransactionRecord.TransactionType.CONSUMPTION);
                record.setAmount(actualAmount.negate());
                record.setBalanceBefore(balanceBefore);
                record.setBalanceAfter(balanceAfter);
                record.setRemark("订单消费: " + order.getOrderNo());
                transactionRecordRepository.save(record);
            } else {
                throw new BusinessException("会员余额不足");
            }
        }

        Order completedOrder = orderRepository.save(order);
        return convertToResponse(completedOrder);
    }

    @Transactional
    public OrderResponse startOrder(Long merchantId, Long orderId) {
        Order order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessException("订单状态不正确，无法开始");
        }

        order.setStatus(Order.OrderStatus.IN_PROGRESS);
        order.setStartTime(LocalDateTime.now());

        Order updatedOrder = orderRepository.save(order);
        return convertToResponse(updatedOrder);
    }

    @Transactional
    public OrderResponse cancelOrder(Long merchantId, Long orderId, String reason) {
        Order order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new BusinessException("订单不存在"));

        if (order.getStatus() == Order.OrderStatus.COMPLETED) {
            throw new BusinessException("已完成的订单不能取消");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setRemark(order.getRemark() + " [取消原因: " + reason + "]");

        Order cancelledOrder = orderRepository.save(order);
        return convertToResponse(cancelledOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long merchantId, Long orderId) {
        Order order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        return convertToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(Long merchantId) {
        return orderRepository.findByMerchantId(merchantId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrders(Long merchantId, Pageable pageable) {
        return orderRepository.findByMerchantId(merchantId, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(Long merchantId, Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByMerchantIdAndStatus(merchantId, status, pageable)
                .map(this::convertToResponse);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMemberOrders(Long merchantId, Long memberId) {
        return orderRepository.findByMerchantIdAndMemberId(merchantId, memberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getBarberOrders(Long merchantId, Long barberId) {
        return orderRepository.findByMerchantIdAndBarberId(merchantId, barberId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());

        if (order.getMember() != null) {
            MemberSimpleResponse memberResponse = new MemberSimpleResponse();
            memberResponse.setId(order.getMember().getId());
            memberResponse.setName(order.getMember().getName());
            memberResponse.setPhone(order.getMember().getPhone());
            response.setMember(memberResponse);
        }

        BarberSimpleResponse barberResponse = new BarberSimpleResponse();
        barberResponse.setId(order.getBarber().getId());
        barberResponse.setName(order.getBarber().getName());
        response.setBarber(barberResponse);

        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setActualAmount(order.getActualAmount());
        response.setCommissionAmount(order.getCommissionAmount());
        response.setStatus(order.getStatus());
        response.setAppointmentTime(order.getAppointmentTime());
        response.setStartTime(order.getStartTime());
        response.setEndTime(order.getEndTime());
        response.setRemark(order.getRemark());

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::convertItemToResponse)
                .collect(Collectors.toList());
        response.setItems(itemResponses);

        response.setCreatedAt(order.getCreatedAt());
        return response;
    }

    private OrderItemResponse convertItemToResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setHairstyleTypeId(item.getHairstyleType().getId());
        response.setHairstyleName(item.getHairstyleName());
        response.setUnitPrice(item.getUnitPrice());
        response.setQuantity(item.getQuantity());
        response.setSubtotal(item.getSubtotal());

        List<OrderItemDetailResponse> detailResponses = item.getDetails().stream()
                .map(this::convertItemDetailToResponse)
                .collect(Collectors.toList());
        response.setDetails(detailResponses);

        return response;
    }

    private OrderItemDetailResponse convertItemDetailToResponse(OrderItemDetail detail) {
        OrderItemDetailResponse response = new OrderItemDetailResponse();
        response.setId(detail.getId());
        response.setDetailName(detail.getDetailName());
        response.setAdditionalPrice(detail.getAdditionalPrice());
        return response;
    }
}
