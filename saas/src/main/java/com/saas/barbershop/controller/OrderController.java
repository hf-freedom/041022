package com.saas.barbershop.controller;

import com.saas.barbershop.dto.*;
import com.saas.barbershop.entity.Order;
import com.saas.barbershop.service.OrderService;
import com.saas.barbershop.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "订单管理", description = "订单创建、查询、完成等接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单", description = "创建新订单")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        OrderResponse response = orderService.createOrder(merchantId, request);
        return ResponseEntity.ok(ApiResponse.success("订单创建成功", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详情")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        OrderResponse response = orderService.getOrder(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "获取订单列表", description = "分页获取订单列表")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(Pageable pageable) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        Page<OrderResponse> response = orderService.getOrders(merchantId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有订单", description = "获取所有订单")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<OrderResponse> response = orderService.getAllOrders(merchantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态获取订单", description = "根据状态获取订单")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrdersByStatus(
            @Parameter(description = "状态: PENDING-待服务, IN_PROGRESS-服务中, COMPLETED-已完成, CANCELLED-已取消")
            @PathVariable Order.OrderStatus status,
            Pageable pageable) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        Page<OrderResponse> response = orderService.getOrdersByStatus(merchantId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "获取会员订单", description = "获取指定会员的所有订单")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMemberOrders(@PathVariable Long memberId) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<OrderResponse> response = orderService.getMemberOrders(merchantId, memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/barber/{barberId}")
    @Operation(summary = "获取理发师订单", description = "获取指定理发师的所有订单")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getBarberOrders(@PathVariable Long barberId) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        List<OrderResponse> response = orderService.getBarberOrders(merchantId, barberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "开始订单", description = "开始服务订单")
    public ResponseEntity<ApiResponse<OrderResponse>> startOrder(@PathVariable Long id) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        OrderResponse response = orderService.startOrder(merchantId, id);
        return ResponseEntity.ok(ApiResponse.success("订单开始服务", response));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "完成订单", description = "完成服务订单")
    public ResponseEntity<ApiResponse<OrderResponse>> completeOrder(
            @PathVariable Long id,
            @RequestBody OrderCompleteRequest request) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        OrderResponse response = orderService.completeOrder(merchantId, id, request);
        return ResponseEntity.ok(ApiResponse.success("订单已完成", response));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消订单")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id,
            @RequestParam String reason) {
        Long merchantId = SecurityUtils.getCurrentMerchantId();
        OrderResponse response = orderService.cancelOrder(merchantId, id, reason);
        return ResponseEntity.ok(ApiResponse.success("订单已取消", response));
    }
}
