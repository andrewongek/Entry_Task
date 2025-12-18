package com.entry_task.entry_task.order.service;

import com.entry_task.entry_task.common.dto.Metadata;
import com.entry_task.entry_task.order.dto.OrderInvoiceItemListResponse;
import com.entry_task.entry_task.order.dto.OrderInvoiceItemResponse;
import com.entry_task.entry_task.order.dto.SellerOrderInvoiceItemListRequest;
import com.entry_task.entry_task.order.entity.OrderItem;
import com.entry_task.entry_task.order.repository.OrderItemRepository;
import com.entry_task.entry_task.order.specifications.OrderItemSpecifications;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class OrderInvoiceCacheService {
    private final OrderItemRepository orderItemRepository;

    public OrderInvoiceCacheService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Cacheable(
            value = "orderitem:list",
            keyGenerator = "sha256KeyGenerator"
    )
    public OrderInvoiceItemListResponse getSellerOrderInvoiceItemList(SellerOrderInvoiceItemListRequest request, Long sellerId) {
        Sort.Direction sortDirection =
                Sort.Direction.fromString(request.sort().order());

        Pageable pageable = PageRequest.of(
                request.pagination().page(),
                request.pagination().size(),
                Sort.by(sortDirection, request.sort().field())
        );

        Specification<OrderItem> spec = OrderItemSpecifications.belongsToSeller(sellerId);
        if (request.filter() != null) {
            if (request.filter().keyword() != null && !request.filter().keyword().isBlank()) {
                spec = spec.and(OrderItemSpecifications.productNameContains(request.filter().keyword()));
            }
            if (request.filter().userId() != null) {
                spec = spec.and(OrderItemSpecifications.userIdEquals(request.filter().userId()));
            }
            if (request.filter().orderId() != null) {
                spec = spec.and(OrderItemSpecifications.orderIdEquals(request.filter().orderId()));
            }
            if (request.filter().orderItemId() != null) {
                spec = spec.and(OrderItemSpecifications.orderItemIdEquals(request.filter().orderItemId()));
            }
            if (request.filter().statuses() != null && !request.filter().statuses().isEmpty()) {
                spec = spec.and(OrderItemSpecifications.orderStatusIn(request.filter().statuses()));
            }
        }

        Page<OrderInvoiceItemResponse> page = orderItemRepository.findAll(spec, pageable).map(orderItem -> new OrderInvoiceItemResponse(
                orderItem.getId(),
                orderItem.getOrder().getId(),
                orderItem.getOrder().getId(),
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getQuantity() * orderItem.getPrice(),
                orderItem.getOrder().getStatus().name(),
                orderItem.getOrder().getcTime(),
                orderItem.getOrder().getmTime()
        ));

        return new OrderInvoiceItemListResponse(
                page.toList(),
                new Metadata(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getSize(),
                        page.hasNext()
                )
        );
    }

}
