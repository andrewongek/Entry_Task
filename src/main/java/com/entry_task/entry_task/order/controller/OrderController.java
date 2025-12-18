package com.entry_task.entry_task.order.controller;

import com.entry_task.entry_task.common.api.CustomApiResponse;
import com.entry_task.entry_task.order.dto.CreateOrderRequest;
import com.entry_task.entry_task.order.dto.OrderListRequest;
import com.entry_task.entry_task.order.dto.OrderListResponse;
import com.entry_task.entry_task.order.dto.OrderResponse;
import com.entry_task.entry_task.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Order", description = "Endpoints for order checkout and order retrieval")
@Validated
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Create a new order (checkout)",
            description = "Creates a new order for the authenticated user. Returns the created order details."
    )
    @PostMapping("/checkout")
    public ResponseEntity<CustomApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", response));
    }

    @Operation(
            summary = "Get list of user orders",
            description = "Retrieves a list of orders for the authenticated user, filtered and paginated according to the request."
    )
    @PostMapping("/search")
    public ResponseEntity<CustomApiResponse<OrderListResponse>> getUserOrdersList(@RequestBody OrderListRequest request) {
        OrderListResponse response = orderService.getUserOrdersList(request);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", response));
    }
}
