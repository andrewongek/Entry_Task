package com.entry_task.entry_task.common;

import com.entry_task.entry_task.category.entity.Category;
import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.favourite.entity.UserFavourite;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.user.entity.User;

import java.time.Instant;
import java.util.Set;

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
}
