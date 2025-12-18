package com.entry_task.entry_task.order.repository;

import com.entry_task.entry_task.order.entity.Order;
import com.entry_task.entry_task.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, JpaSpecificationExecutor<OrderItem> {
}
