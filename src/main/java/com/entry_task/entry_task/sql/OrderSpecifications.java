package com.entry_task.entry_task.sql;

import com.entry_task.entry_task.enums.OrderStatus;
import com.entry_task.entry_task.model.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class OrderSpecifications {

    public static Specification<Order> belongsToUser(Long userId) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Order> statusIn(List<String> statuses) {
        return (root, query, cb) ->
                root.get("status").in(statuses);
    }
}
