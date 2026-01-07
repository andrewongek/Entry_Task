package com.entry_task.entry_task.favourite.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.common.TestEntityFactory;
import com.entry_task.entry_task.exceptions.FavouriteNotFoundException;
import com.entry_task.entry_task.exceptions.ProductAlreadyFavouritedException;
import com.entry_task.entry_task.favourite.entity.UserFavourite;
import com.entry_task.entry_task.favourite.repository.UserFavouriteRepository;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.product.service.ProductService;
import com.entry_task.entry_task.user.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserFavouriteServiceTest {

  @InjectMocks private UserFavouriteService userFavouriteService;

  @Mock private UserFavouriteRepository userFavouriteRepository;

  @Mock private ProductService productService;

  @Mock private AuthService authService;

  @Test
  void
      setUserFavouriteByProductId_productAlreadyFavourited_shouldThrowProductAlreadyFavouritedException() {
    // Given
    User customer = TestEntityFactory.createCustomerWithId("cutomer");
    User seller = TestEntityFactory.createSellerWithId("seller");
    Product product = TestEntityFactory.createProductWithId("product", seller);
    UserFavourite userFavourite = TestEntityFactory.createUserFavourite(customer, product);

    // When
    when(productService.getActiveProductById(product.getId())).thenReturn(product);
    when(authService.getCurrentUser()).thenReturn(customer);
    when(userFavouriteRepository.findByUserIdAndProductId(customer.getId(), product.getId()))
        .thenReturn(Optional.of(userFavourite));

    // Then
    ProductAlreadyFavouritedException ex =
        assertThrows(
            ProductAlreadyFavouritedException.class,
            () -> userFavouriteService.setUserFavouriteByProductId(product.getId()));
    assertEquals("Product is already in User's Favourites", ex.getMessage());
  }

  @Test
  void
      deleteUserFavouriteByProductId_productAlreadyNotFavourited_shouldThrowFavouriteNotFoundException() {
    // Given
    User customer = TestEntityFactory.createCustomerWithId("cutomer");
    User seller = TestEntityFactory.createSellerWithId("seller");
    Product product = TestEntityFactory.createProductWithId("product", seller);

    // When
    when(productService.getActiveProductById(product.getId())).thenReturn(product);
    when(authService.getCurrentUser()).thenReturn(customer);
    when(userFavouriteRepository.findByUserIdAndProductId(customer.getId(), product.getId()))
        .thenReturn(Optional.empty());

    // Then
    FavouriteNotFoundException ex =
        assertThrows(
            FavouriteNotFoundException.class,
            () -> userFavouriteService.deleteUserFavouriteByProductId(product.getId()));
    assertEquals("Product is already not favourited by User", ex.getMessage());
  }
}
