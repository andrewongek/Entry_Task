package com.entry_task.entry_task.order.specifications;

import com.entry_task.entry_task.order.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class  OrderSpecifications {

    public static Specification<Order> belongsToUser(Long userId) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Order> statusIn(List<String> statuses) {
        return (root, query, cb) ->
                root.get("status").in(statuses);
    }
}
