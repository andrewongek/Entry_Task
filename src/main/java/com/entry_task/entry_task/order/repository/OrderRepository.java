package com.entry_task.entry_task.order.repository;

import com.entry_task.entry_task.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByUserIdAndIdempotencyKey(Long userId, String idempotencyKey);

    // NOTE: Fetching Orders with their OrderItems does not scale well when orders have many items.
    // Pagination + @OneToMany fetch can cause row duplication, in-memory pagination, and large memory usage.
    // If orders grow large, switch to a 2-step approach:
    //   1) Page only Order IDs
    //   2) Fetch full order graph (items + products) by ID IN (...)
    // Keep list endpoints lightweight; fetch full items only in order-detail endpoints.
    @EntityGraph(attributePaths = {"items"})
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);
}
