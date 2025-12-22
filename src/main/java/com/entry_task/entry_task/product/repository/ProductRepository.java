package com.entry_task.entry_task.product.repository;

import com.entry_task.entry_task.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
