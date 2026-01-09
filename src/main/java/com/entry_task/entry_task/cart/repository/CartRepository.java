package com.entry_task.entry_task.cart.repository;

import com.entry_task.entry_task.cart.entity.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
  Optional<Cart> findByUserId(Long userId);
}
