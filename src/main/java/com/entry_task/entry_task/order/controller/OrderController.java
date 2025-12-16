package com.entry_task.entry_task.order.controller;

import com.entry_task.entry_task.common.api.ApiResponse;
import com.entry_task.entry_task.order.dto.CreateOrderRequest;
import com.entry_task.entry_task.order.dto.OrderListRequest;
import com.entry_task.entry_task.order.dto.OrderListResponse;
import com.entry_task.entry_task.order.dto.OrderResponse;
import com.entry_task.entry_task.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok().body(ApiResponse.success("success", response));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<OrderListResponse>> getUserOrdersList(@RequestBody OrderListRequest request) {
        OrderListResponse response = orderService.getUserOrdersList(request);
        return ResponseEntity.ok().body(ApiResponse.success("success", response));
    }
}
