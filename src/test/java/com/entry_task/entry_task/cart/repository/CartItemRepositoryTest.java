package com.entry_task.entry_task.cart.repository;

import com.entry_task.entry_task.cart.dto.CartItemDto;
import com.entry_task.entry_task.cart.entity.Cart;
import com.entry_task.entry_task.cart.entity.CartItem;
import com.entry_task.entry_task.common.TestEntityFactory;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.product.repository.ProductRepository;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CartItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Test
    void findByCartIdAndProductId_cartItemExist_shouldReturnCartItem() {
        User seller = TestEntityFactory.createSeller("seller");
        userRepository.save(seller);
        User customer = TestEntityFactory.createCustomer("customer");
        userRepository.save(customer);
        Product product = TestEntityFactory.createProduct("testProduct", seller);
        productRepository.save(product);
        Cart cart = TestEntityFactory.createEmptyCart(customer);
        cartRepository.save(cart);
        CartItem cartItem = TestEntityFactory.createCartItem(cart, product);
        cartItemRepository.save(cartItem);

        Optional<CartItem> result = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        assertTrue(result.isPresent());
    }

    @Test
    void findByCartIdAndProductId_cartDoesNotExist_shouldNotReturnCart() {
        User seller = TestEntityFactory.createSeller("seller");
        userRepository.save(seller);
        User customer = TestEntityFactory.createCustomer("customer");
        userRepository.save(customer);
        Product product = TestEntityFactory.createProduct("testProduct", seller);
        productRepository.save(product);
        Cart cart = TestEntityFactory.createEmptyCart(customer);
        cartRepository.save(cart);
        CartItem cartItem = TestEntityFactory.createCartItem(cart, product);
        cartItemRepository.save(cartItem);

        Long nonExistCartId = cart.getId() - 1;

        Optional<CartItem> result = cartItemRepository.findByCartIdAndProductId(nonExistCartId, product.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByCartIdAndProductId_productDoesNotExist_shouldNotReturnCart() {
        User seller = TestEntityFactory.createSeller("seller");
        userRepository.save(seller);
        User customer = TestEntityFactory.createCustomer("customer");
        userRepository.save(customer);
        Product product = TestEntityFactory.createProduct("testProduct", seller);
        productRepository.save(product);
        Cart cart = TestEntityFactory.createEmptyCart(customer);
        cartRepository.save(cart);
        CartItem cartItem = TestEntityFactory.createCartItem(cart, product);
        cartItemRepository.save(cartItem);

        Long nonExistProductId = product.getId() - 1;

        Optional<CartItem> result = cartItemRepository.findByCartIdAndProductId(cart.getId(), nonExistProductId);

        assertTrue(result.isEmpty());
    }

    @Test
    void findCartItemProjections() {
        User seller = TestEntityFactory.createSeller("seller1");
        userRepository.save(seller);
        User seller2 = TestEntityFactory.createSeller("seller2");
        userRepository.save(seller2);
        User customer = TestEntityFactory.createCustomer("customer");
        userRepository.save(customer);
        Product product1 = TestEntityFactory.createProduct("testProduct1", seller);
        productRepository.save(product1);
        Product product2 = TestEntityFactory.createProduct("testProduct2", seller);
        productRepository.save(product2);
        Product product3 = TestEntityFactory.createProduct("testProduct3", seller2);
        productRepository.save(product3);


        Cart cart = TestEntityFactory.createEmptyCart(customer);
        cartRepository.save(cart);


        CartItem cartItem = TestEntityFactory.createCartItem(cart, product1);
        cartItemRepository.save(cartItem);
        CartItem cartItem2 = TestEntityFactory.createCartItem(cart, product2);
        cartItemRepository.save(cartItem2);
        CartItem cartItem3 = TestEntityFactory.createCartItem(cart, product3);
        cartItemRepository.save(cartItem3);

        CartItemDto expected1 = new CartItemDto(
                cartItem.getId(),
                cartItem.getQuantity(),
                product1.getId(),
                product1.getName(),
                product1.getPrice(),
                product1.getStock(),
                product1.getSeller().getId(),
                cartItem.getQuantity() * product1.getPrice(),
                cart.getmTime()
        );

        CartItemDto expected2 = new CartItemDto(
                cartItem2.getId(),
                cartItem2.getQuantity(),
                product2.getId(),
                product2.getName(),
                product2.getPrice(),
                product2.getStock(),
                product2.getSeller().getId(),
                cartItem2.getQuantity() * product2.getPrice(),
                cart.getmTime()
        );

        CartItemDto expected3 = new CartItemDto(
                cartItem3.getId(),
                cartItem3.getQuantity(),
                product3.getId(),
                product3.getName(),
                product3.getPrice(),
                product3.getStock(),
                product3.getSeller().getId(),
                cartItem3.getQuantity() * product3.getPrice(),
                cart.getmTime()
        );


        List<CartItemDto> result  = cartItemRepository.findCartItemDtos(customer.getId());

        assertTrue(result.containsAll(List.of(expected1, expected2, expected3)),
                "CartItemDto list should contain all expected DTOs");

    }

    @Test
    void findAllByIdAndUser() {
    }
}