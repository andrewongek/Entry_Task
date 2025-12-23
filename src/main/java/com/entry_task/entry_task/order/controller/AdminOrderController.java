package com.entry_task.entry_task.order.controller;

import com.entry_task.entry_task.common.api.CustomApiResponse;
import com.entry_task.entry_task.order.dto.AdminOrderInvoiceItemListRequest;
import com.entry_task.entry_task.order.dto.OrderInvoiceItemListResponse;
import com.entry_task.entry_task.order.service.OrderService;
import com.entry_task.entry_task.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Order", description = "Endpoints for admin order retrieval")
@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Get list of order invoice items",
            description = "Retrieves a list of order item details, filtered and paginated according to the request."
    )
    @PostMapping("/search")
    public ResponseEntity<CustomApiResponse<OrderInvoiceItemListResponse>> getOrderInvoiceItemList (@RequestBody AdminOrderInvoiceItemListRequest request) {
        log.info("Request: {}",request);
        OrderInvoiceItemListResponse response = orderService.getOrderInvoiceItemList(request);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", response));
    }

}
