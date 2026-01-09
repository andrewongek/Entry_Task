package com.entry_task.entry_task.order.specifications;

import com.entry_task.entry_task.enums.OrderStatus;
import com.entry_task.entry_task.order.entity.OrderItem;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class OrderItemSpecifications {
  public static Specification<OrderItem> belongsToSeller(Long sellerId) {
    return (root, query, criteriaBuilder) ->
        sellerId == null
            ? null
            : criteriaBuilder.equal(root.get("product").get("seller").get("id"), sellerId);
  }

  public static Specification<OrderItem> productNameContains(String keyword) {
    return (root, query, criteriaBuilder) ->
        keyword == null
            ? null
            : criteriaBuilder.like(
                criteriaBuilder.lower(root.get("product").get("name")),
                "%" + keyword.toLowerCase() + "%");
  }

  public static Specification<OrderItem> userIdEquals(Long userId) {
    return (root, query, criteriaBuilder) ->
        userId == null
            ? null
            : criteriaBuilder.equal(root.get("order").get("user").get("id"), userId);
  }

  public static Specification<OrderItem> orderIdEquals(Long orderId) {
    return (root, query, criteriaBuilder) ->
        orderId == null ? null : criteriaBuilder.equal(root.get("order").get("id"), orderId);
  }

  public static Specification<OrderItem> orderItemIdEquals(Long orderItemId) {
    return (root, query, criteriaBuilder) ->
        orderItemId == null ? null : criteriaBuilder.equal(root.get("id"), orderItemId);
  }

  // TODO : consider create index in db on Order Status field for better performance
  public static Specification<OrderItem> orderStatusIn(List<OrderStatus> statuses) {
    return (root, query, criteriaBuilder) ->
        statuses == null || statuses.isEmpty()
            ? null
            : root.get("order").get("status").in(statuses);
  }
}
