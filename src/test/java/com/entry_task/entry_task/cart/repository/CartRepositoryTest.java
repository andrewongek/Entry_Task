package com.entry_task.entry_task.cart.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.entry_task.entry_task.cart.entity.Cart;
import com.entry_task.entry_task.common.TestEntityFactory;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class CartRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Autowired private CartRepository cartRepository;

  @Test
  void findByUserId_cartExists_shouldReturnCart() {
    // Given
    User customer = userRepository.save(TestEntityFactory.createCustomer("customer"));
    Cart cart = cartRepository.save(TestEntityFactory.createEmptyCart(customer));

    // When
    Optional<Cart> result = cartRepository.findByUserId(customer.getId());

    // Then
    assertTrue(result.isPresent());
    assertEquals(cart, result.get());
  }

  @Test
  void findByUserId_cartDoesNotExists_shouldNotReturnCart() {
    // Given
    Long CustomerIdWithNoCart = 1L;

    // When
    Optional<Cart> result = cartRepository.findByUserId(CustomerIdWithNoCart);

    // Then
    assertTrue(result.isEmpty());
  }
}
