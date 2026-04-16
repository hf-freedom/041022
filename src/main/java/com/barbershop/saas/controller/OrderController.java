package com.barbershop.saas.controller;

import com.barbershop.saas.common.Result;
import com.barbershop.saas.dto.CreateOrderDTO;
import com.barbershop.saas.entity.Order;
import com.barbershop.saas.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<Order> createOrder(@Valid @RequestBody CreateOrderDTO dto) {
        Order order = orderService.createOrder(dto);
        return Result.success(order);
    }

    @GetMapping("/list")
    public Result<List<Order>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        List<Order> list = orderService.list(keyword, status);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Order> getDetail(@PathVariable Long id) {
        Order order = orderService.getDetail(id);
        return Result.success(order);
    }
}
