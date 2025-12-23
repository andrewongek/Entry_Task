package com.entry_task.entry_task.product.repository;

import com.entry_task.entry_task.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
                UPDATE Product p
                SET p.stock = p.stock - :qty
                WHERE p.id = :id
                  AND p.stock >= :qty
                  AND p.productStatus = com.entry_task.entry_task.enums.ProductStatus.ACTIVE
            """)
    int reserveStock(@Param("id") long id, @Param("qty") int qty);

    @EntityGraph(attributePaths = "seller")
    @NonNull
    Page<Product> findAll(
            @Nullable Specification<Product> spec,
            @NonNull Pageable pageable
    );
}
