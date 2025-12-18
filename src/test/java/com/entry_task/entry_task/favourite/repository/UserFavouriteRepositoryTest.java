package com.entry_task.entry_task.favourite.repository;

import com.entry_task.entry_task.common.TestEntityFactory;
import com.entry_task.entry_task.favourite.entity.UserFavourite;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.product.repository.ProductRepository;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class UserFavouriteRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserFavouriteRepository favouriteRepository;

    @Test
    void findByUserIdAndProductId_shouldReturnUserFavourite() {

        User customer = TestEntityFactory.createCustomer("customer");
        userRepository.save(customer);
        User seller = TestEntityFactory.createSeller("seller");
        userRepository.save(seller);
        Product product = TestEntityFactory.createProduct("testProduct", seller);
        productRepository.save(product);
        UserFavourite userFavourite = TestEntityFactory.createUserFavourite(customer, product);
        favouriteRepository.save(userFavourite);

        Optional<UserFavourite> result =  favouriteRepository.findByUserIdAndProductId(customer.getId(), product.getId());

        assertTrue(result.isPresent());
        assertEquals(userFavourite, result.get());
    }

    @Test
    void findByUserIdAndProductId_userIdNotFound_shouldNotReturnUserFavourite() {
        User customer = TestEntityFactory.createCustomer("customer");
        userRepository.save(customer);
        User seller = TestEntityFactory.createSeller("seller");
        userRepository.save(seller);
        Product product = TestEntityFactory.createProduct("testProduct", seller);
        productRepository.save(product);
        UserFavourite userFavourite = TestEntityFactory.createUserFavourite(customer, product);
        favouriteRepository.save(userFavourite);

        Long invalidUserId = customer.getId()-1;

        Optional<UserFavourite> result =  favouriteRepository.findByUserIdAndProductId(invalidUserId, product.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserIdAndProductId_productIdNotFound_shouldNotReturnUserFavourite() {
        User customer = TestEntityFactory.createCustomer("customer");
        userRepository.save(customer);
        User seller = TestEntityFactory.createSeller("seller");
        userRepository.save(seller);
        Product product = TestEntityFactory.createProduct("testProduct", seller);
        productRepository.save(product);
        UserFavourite userFavourite = TestEntityFactory.createUserFavourite(customer, product);
        favouriteRepository.save(userFavourite);

        Long invalidProductIdId = product.getId()-1;

        Optional<UserFavourite> result =  favouriteRepository.findByUserIdAndProductId(customer.getId(), invalidProductIdId);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserIdAndProductId_userFavouriteNotFound_shouldNotReturnUserFavourite() {
        Long userId = 1L;
        Long productId = 1L;
        Optional<UserFavourite> result =  favouriteRepository.findByUserIdAndProductId(userId, productId);

        assertTrue(result.isEmpty());
    }
}