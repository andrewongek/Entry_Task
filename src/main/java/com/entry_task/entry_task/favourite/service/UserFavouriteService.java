package com.entry_task.entry_task.favourite.service;

import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.auth.service.AuthServiceImpl;
import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.exceptions.FavouriteNotFoundException;
import com.entry_task.entry_task.exceptions.ProductAlreadyFavouritedException;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.favourite.entity.UserFavourite;
import com.entry_task.entry_task.product.dto.ProductListRequest;
import com.entry_task.entry_task.product.dto.ProductListResponse;
import com.entry_task.entry_task.product.dto.ProductListing;
import com.entry_task.entry_task.product.service.ProductService;
import com.entry_task.entry_task.favourite.repository.UserFavouriteRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserFavouriteService {

    private static final Logger log = LoggerFactory.getLogger(UserFavouriteService.class);

    private final UserFavouriteRepository userFavouriteRepository;
    private final ProductService productService;
    private final AuthService authService;

    public UserFavouriteService(UserFavouriteRepository userFavouriteRepository, ProductService productService, AuthService authService) {
        this.userFavouriteRepository = userFavouriteRepository;
        this.productService = productService;
        this.authService = authService;
    }

    @PreAuthorize("hasRole('USER')")
    public ProductListResponse<ProductListing> getUserFavouriteProductListingList(ProductListRequest request) {
        return productService.getUserFavouriteProductListingList(request);
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public void setUserFavouriteByProductId(Long productId) {
        Product product = productService.getActiveProductById(productId);
        User user = authService.getCurrentUser();
        Optional<UserFavourite> exists = userFavouriteRepository.findByUserIdAndProductId(user.getId(), product.getId());
        if (exists.isPresent()) {
            throw new ProductAlreadyFavouritedException();
        }
        setUserFavourite(user, product);
        log.debug("User add to favourite: userId={}, productId={}", user.getId(), product.getId());
    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public void deleteUserFavouriteByProductId(Long productId) {
        Product product = productService.getActiveProductById(productId);
        User user = authService.getCurrentUser();
        if (user.getRole() != Role.USER) {
            throw new AccessDeniedException("Only normal users can favourite products");
        }
        UserFavourite userFavourite = userFavouriteRepository.findByUserIdAndProductId(user.getId(), product.getId()).orElseThrow(() -> new FavouriteNotFoundException("Product is already not favourited by User"));
        deleteUserFavourite(userFavourite);
        log.debug("User removed favourite: userId={}, productId={}", user.getId(), product.getId());
    }


    private void setUserFavourite(User user, Product product) {
        UserFavourite userFavourite = new UserFavourite();
        userFavourite.setUser(user);
        userFavourite.setProduct(product);
        userFavouriteRepository.save(userFavourite);
    }

    private void deleteUserFavourite(UserFavourite userFavourite) {
        userFavouriteRepository.delete(userFavourite);
    }
}
