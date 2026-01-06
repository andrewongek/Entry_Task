package com.entry_task.entry_task.product.specifications;

import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.favourite.entity.UserFavourite;
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

    public static Specification<Product> statusIn(List<Integer> statuses) {
        return (root, query, criteriaBuilder) ->
                root.get("productStatus").in(statuses);
    }

    public static Specification<Product> statusIsActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("productStatus"), ProductStatus.ACTIVE);
    }

    public static Specification<Product> categoryIn(List<Long> categoryIds) {
        return (root, query, criteriaBuilder) -> {
            var subquery = query.subquery(Long.class);
            var productSubRoot = subquery.from(Product.class);
            var categoryJoin = productSubRoot.join("categories");

            subquery.select(criteriaBuilder.literal(1L))
                    .where(
                            criteriaBuilder.equal(productSubRoot.get("id"), root.get("id")),
                            categoryJoin.get("id").in(categoryIds)
                    );

            return criteriaBuilder.exists(subquery);
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
