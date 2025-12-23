package com.entry_task.entry_task.product.repository;

import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.product.repository.projections.ProductDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

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

    @Query("""
                select count(p) > 0
                from Product p
                where p.id = :productId
                  and p.seller.id = :sellerId
            """)
    boolean isOwnedBySeller(@Param("productId") long productId,
                            @Param("sellerId") long sellerId);

    @Query("""
                select
                    p.id as id,
                    p.name as name,
                    s.id as sellerId,
                    p.stock as stock,
                    p.price as price,
                    c.id as categoryIds,
                    p.description as description,
                    p.productStatus as productStatus,
                    p.cTime as cTime,
                    p.mTime as mTime
                from Product p
                join p.seller s
                left join p.categories c
                where p.id = :productId
            """)
    Optional<ProductDetailProjection> findProductDetail(@Param("productId") long productId);

    @Modifying
    @Query("""
                update Product p
                set p.productStatus = :newStatus
                where p.id = :productId
                  and p.productStatus = :expectedCurrent
            """)
    int updateStatusIfCurrent(
            @Param("productId") Long productId,
            @Param("expectedCurrent") ProductStatus expectedCurrent,
            @Param("newStatus") ProductStatus newStatus
    );


    @Modifying
    @Query("""
                update Product p
                set
                    p.name = :name
                    p.price = :price
                    p.stock = :stock
                    p.description = :description
                    p.mTime = :mTime
                where p.id = :productId
            """)
    int updateProduct(
            @Param("productId") long productId,
            @Param("name") String name,
            @Param("price") int price,
            @Param("stock") int stock,
            @Param("description") String description,
            @Param("mTime") long mTime
    );
}
