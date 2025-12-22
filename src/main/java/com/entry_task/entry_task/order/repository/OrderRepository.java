package com.entry_task.entry_task.order.repository;

import com.entry_task.entry_task.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByUserIdAndIdempotencyKey(Long userId, String idempotencyKey);
}
