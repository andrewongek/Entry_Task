package com.entry_task.entry_task.cart.repository;

import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.cart.repository.projections.CartItemProjection;
import com.entry_task.entry_task.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Query("""
                SELECT
                    ci.id            AS cartItemId,
                    ci.quantity      AS quantity,
            
                    p.id             AS productId,
                    p.name           AS productName,
                    p.price          AS productPrice,
                    p.stock          AS productStock,
                    p.seller.id      AS sellerId,
            
                    (ci.quantity * p.price) AS subTotalPrice,
                    c.mTime          AS cartUpdatedAt
                FROM CartItem ci
                JOIN ci.product p
                JOIN ci.cart c
                WHERE c.user.id = :userId
                  AND p.productStatus = com.entry_task.entry_task.enums.ProductStatus.ACTIVE
            """)
    List<CartItemProjection> findCartItemProjections(@Param("userId") Long userId);

    @Query("""
                select ci
                from CartItem ci
                join ci.cart c
                where ci.id in :ids
                  and c.user = :user
            """)
    List<CartItem> findAllByIdAndUser(
            @Param("ids") List<Long> ids,
            @Param("user") User customer
    );
}
