package com.entry_task.entry_task.order.controller;

import com.entry_task.entry_task.common.api.CustomApiResponse;
import com.entry_task.entry_task.order.dto.OrderInvoiceItemListResponse;
import com.entry_task.entry_task.order.dto.SellerOrderInvoiceItemListRequest;
import com.entry_task.entry_task.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Seller Order", description = "Endpoints for seller order retrieval")
@RestController
@Validated
@RequestMapping("/api/seller/orders")
public class SellerOrderController {
    private final OrderService orderService;

    public SellerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Get list of order invoice items",
            description = "Retrieves a list of order item details, filtered and paginated according to the request."
    )
    @PostMapping("/search")
    public ResponseEntity<CustomApiResponse<OrderInvoiceItemListResponse>> getOrderInvoiceItemList (@Valid @RequestBody SellerOrderInvoiceItemListRequest request) {
        OrderInvoiceItemListResponse response = orderService.getSellerOrderInvoiceItemList(request);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", response));
    }
}