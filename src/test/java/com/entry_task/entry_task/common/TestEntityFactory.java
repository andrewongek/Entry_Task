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
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TestEntityFactory {
    public static User createCustomer(String username) {
        return createUserWithRole(username, Role.CUSTOMER);
    }

    public static User createSeller(String username) {
        return createUserWithRole(username, Role.SELLER);
    }

    public static User createAdmin(String username) {
        return createUserWithRole(username, Role.ADMIN);
    }

    private static User createUserWithRole(String username, Role role) {
        return new User(username, username + "@mail.com", "pass", role);
    }

    public static Product createProduct(String name, User seller) {
        long now = Instant.now().getEpochSecond();
        return createProduct(name, seller, 10, 100, null, "Test Product", ProductStatus.ACTIVE, now, now);
    }

    public static Product createProduct(String name, User seller, int stock, int price, Set<Category> categoryIds, String description, ProductStatus status, Long cTime, Long mTime) {
        return new Product(
                name,
                seller,
                stock,
                price,
                categoryIds,
                description,
                status,
                cTime,
                mTime
        );
    }

    public static UserFavourite createUserFavourite(User user, Product product){
        return new UserFavourite(user, product);
    }

    public static Cart createEmptyCart(User customer){
        long now = Instant.now().getEpochSecond();
        return createCart(customer, now, now);
    }

    public static Cart createCart(User customer, Long cTime, Long mTime) {
        return new Cart(customer, cTime, mTime);
    }

    public static CartItem createCartItem (Cart cart, Product product) {
        long now = Instant.now().getEpochSecond();
        return createCartItem(cart, product, 10, now, now);
    }

    public static CartItem createCartItem (Cart cart, Product product, int quantity, Long ctime, Long mtime) {
        return new CartItem(cart, product, quantity, ctime, mtime);
    }

    public static RefreshToken createRefreshToken(User user) {
        return createRefreshToken(user, UUID.randomUUID().toString(), Instant.now().plusMillis(86400000));
    }


    public static RefreshToken createRefreshToken (User user, String token, Instant expiryDate) {
        return new RefreshToken(user, token, expiryDate);
    }
}
