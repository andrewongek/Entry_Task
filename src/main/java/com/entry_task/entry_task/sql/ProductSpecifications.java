package com.entry_task.entry_task.sql;

import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.model.Product;
import com.entry_task.entry_task.model.UserFavourite;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecifications {
    public static Specification<Product> belongsToSeller(Long sellerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("seller").get("id"), sellerId);
    }

    public static Specification<Product> nameContains(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Product> statusIn(List<Integer> statusus) {
        return (root, query, criteriaBuilder) ->
                root.get("productStatus").in(statusus);
    }

    public static Specification<Product> statusIsActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("productStatus"), ProductStatus.ACTIVE);
    }

    public static Specification<Product> categoryIn(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            var join = root.join("categories");
            return join.get("id").in(categoryIds);
        };
    }

    public static Specification<Product> isFavouritedBy(Long userId) {
        return (root, query, criteriaBuilder) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            var favouriteRoot = subquery.from(UserFavourite.class);
            subquery.select(favouriteRoot.get("product").get("id"))
                    .where(criteriaBuilder.equal(
                            favouriteRoot.get("user").get("id"),
                            userId
                    ));
            return criteriaBuilder.in(root.get("id")).value(subquery);
        };
    }
}
