package com.entry_task.entry_task.order;

import com.entry_task.entry_task.order.dto.OrderInvoiceItemResponse;
import com.entry_task.entry_task.order.dto.OrderItemResponse;
import com.entry_task.entry_task.order.dto.OrderSummary;
import com.entry_task.entry_task.order.entity.Order;
import com.entry_task.entry_task.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

  @Mapping(target = "orderItemId", source = "id")
  @Mapping(target = "orderId", source = "order.id")
  @Mapping(target = "userId", source = "order.user.id")
  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "productName", source = "product.name")
  @Mapping(
      target = "totalPrice",
      expression = "java(orderItem.getQuantity() * orderItem.getPrice())")
  @Mapping(target = "orderStatus", expression = "java(orderItem.getOrder().getStatus().name())")
  @Mapping(target = "orderCTime", source = "order.CTime")
  @Mapping(target = "orderMTime", source = "order.MTime")
  OrderInvoiceItemResponse toOrderInvoiceItemResponse(OrderItem orderItem);

  @Mapping(target = "orderId", source = "id")
  @Mapping(target = "totalItems", expression = "java(order.getItems().size())")
  @Mapping(
      target = "totalQuantity",
      expression = "java(order.getItems().stream().mapToInt(i -> i.getQuantity()).sum())")
  @Mapping(target = "totalPrice", source = "totalAmount")
  @Mapping(target = "orderItems", source = "items")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "cTime", source = "CTime")
  @Mapping(target = "mTime", source = "MTime")
  OrderSummary toOrderSummary(Order order);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "name", source = "product.name")
  @Mapping(target = "subTotalPrice", expression = "java(item.getQuantity() * item.getPrice())")
  OrderItemResponse toOrderItemResponse(OrderItem item);
}
