package com.entry_task.entry_task.common;

import com.entry_task.entry_task.auth.entity.RefreshToken;
import com.entry_task.entry_task.cart.entity.Cart;
import com.entry_task.entry_task.cart.entity.CartItem;
import com.entry_task.entry_task.category.entity.Category;
import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.favourite.entity.UserFavourite;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.user.entity.User;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class TestEntityFactory {
  public static User createCustomer(String username) {
    return createUserWithRole(username, Role.CUSTOMER);
  }

  public static User createCustomerWithId(String username) {
    User user = createCustomer(username);
    user.setId(1L);
    return user;
  }

  public static User createCustomerWithId(String username, Long id) {
    User user = createCustomer(username);
    user.setId(id);
    return user;
  }

  public static User createSeller(String username) {
    return createUserWithRole(username, Role.SELLER);
  }

  public static User createSellerWithId(String username) {
    User user = createSeller(username);
    user.setId(1L);
    return user;
  }

  public static User createSellerWithId(String username, Long id) {
    User user = createSeller(username);
    user.setId(id);
    return user;
  }

  public static User createAdminWithId(String username, Long id) {
    User user = createUserWithRole(username, Role.ADMIN);
    user.setId(id);
    return user;
  }

  public static User createAdminWithId(String username) {
    User user = createUserWithRole(username, Role.ADMIN);
    user.setId(1L);
    return user;
  }

  public static User createAdmin(String username) {
    return createUserWithRole(username, Role.ADMIN);
  }

  private static User createUserWithRole(String username, Role role) {
    return new User(username, username + "@mail.com", "pass", role);
  }

  public static Product createProductWithId(String name, User seller) {
    Product product = createProduct(name, seller);
    product.setId(1L);
    return product;
  }

  public static Product createProductWithId(String name, User seller, Long id) {
    Product product = createProduct(name, seller);
    product.setId(id);
    return product;
  }

  public static Product createProduct(String name, User seller) {
    long now = Instant.now().getEpochSecond();
    return createProduct(
        name, seller, 10, 100, null, "Test Product", ProductStatus.ACTIVE, now, now);
  }

  public static Product createProduct(
      String name,
      User seller,
      int stock,
      int price,
      Set<Category> categoryIds,
      String description,
      ProductStatus status,
      Long cTime,
      Long mTime) {
    return new Product(name, seller, stock, price, categoryIds, description, status, cTime, mTime);
  }

  public static UserFavourite createUserFavourite(User user, Product product) {
    return new UserFavourite(user, product);
  }

  public static Cart createEmptyCart(User customer) {
    long now = Instant.now().getEpochSecond();
    return createCart(customer, now, now);
  }

  public static Cart createCart(User customer, Long cTime, Long mTime) {
    return new Cart(customer, cTime, mTime);
  }

  public static CartItem createCartItem(Cart cart, Product product) {
    return createCartItem(cart, product, 2);
  }

  public static CartItem createCartItem(Cart cart, Product product, int quantity) {
    long now = Instant.now().getEpochSecond();
    return createCartItem(cart, product, quantity, now, now);
  }

  public static CartItem createCartItem(
      Cart cart, Product product, int quantity, Long ctime, Long mtime) {
    return new CartItem(cart, product, quantity, ctime, mtime);
  }

  public static RefreshToken createRefreshToken(User user) {
    return createRefreshToken(
        user, UUID.randomUUID().toString(), Instant.now().plusMillis(86400000));
  }

  public static RefreshToken createRefreshToken(User user, String token, Instant expiryDate) {
    return new RefreshToken(user, token, expiryDate);
  }

  public static Category createCategory(String name) {
    return new Category(name);
  }
}
