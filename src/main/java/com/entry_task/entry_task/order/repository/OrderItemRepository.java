package com.entry_task.entry_task.order.repository;

import com.entry_task.entry_task.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
