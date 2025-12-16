package com.entry_task.entry_task.favourite.controller;

import com.entry_task.entry_task.common.api.ApiResponse;
import com.entry_task.entry_task.favourite.service.UserFavouriteService;
import com.entry_task.entry_task.product.dto.ProductListRequest;
import com.entry_task.entry_task.product.dto.ProductListResponse;
import com.entry_task.entry_task.product.dto.ProductListing;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favourites")
public class FavouriteController {
    private final UserFavouriteService userFavouriteService;

    public FavouriteController(UserFavouriteService userFavouriteService) {
        this.userFavouriteService = userFavouriteService;
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<ProductListResponse<ProductListing>>> getUserFavouriteProductListingList(@RequestBody ProductListRequest request) {
        ProductListResponse<ProductListing> responseData = userFavouriteService.getUserFavouriteProductListingList(request);
        return ResponseEntity.ok().body(ApiResponse.success("success", responseData));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> setUserFavouriteByProductId(@PathVariable Long productId) {
        userFavouriteService.setUserFavouriteByProductId(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteUserFavouriteByProductId(@PathVariable Long productId) {
        userFavouriteService.deleteUserFavouriteByProductId(productId);
        return ResponseEntity.ok().body(ApiResponse.success("success", null));
    }
}
