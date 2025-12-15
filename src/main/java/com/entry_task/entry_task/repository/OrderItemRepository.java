package com.entry_task.entry_task.repository;

import com.entry_task.entry_task.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
