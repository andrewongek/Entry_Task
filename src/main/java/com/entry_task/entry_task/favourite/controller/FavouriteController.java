package com.entry_task.entry_task.favourite.controller;

import com.entry_task.entry_task.common.api.CustomApiResponse;
import com.entry_task.entry_task.favourite.service.UserFavouriteService;
import com.entry_task.entry_task.product.dto.ProductListRequest;
import com.entry_task.entry_task.product.dto.ProductListResponse;
import com.entry_task.entry_task.product.dto.ProductListing;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Favourites", description = "Endpoints for managing user favourite products")
@RestController
@RequestMapping("/api/favourites")
public class FavouriteController {
    private final UserFavouriteService userFavouriteService;

    public FavouriteController(UserFavouriteService userFavouriteService) {
        this.userFavouriteService = userFavouriteService;
    }

    @Operation(
            summary = "Get user's favourite product listing",
            description = "Retrieve a paginated list of products favourited by the authenticated user"
    )
    @PostMapping("/search")
    public ResponseEntity<CustomApiResponse<ProductListResponse<ProductListing>>> getUserFavouriteProductListingList(@RequestBody ProductListRequest request) {
        ProductListResponse<ProductListing> responseData = userFavouriteService.getUserFavouriteProductListingList(request);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", responseData));
    }

    @Operation(
            summary = "Add product to user's favourites",
            description = "Mark a product as favourite for the authenticated user using the productId"
    )
    @PostMapping("/{productId}")
    public ResponseEntity<CustomApiResponse<Void>> setUserFavouriteByProductId(@PathVariable Long productId) {
        userFavouriteService.setUserFavouriteByProductId(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", null));
    }

    @Operation(
            summary = "Remove product from user's favourites",
            description = "Unmark a product as favourite for the authenticated user using the productId"
    )
    @DeleteMapping("/{productId}")
    public ResponseEntity<CustomApiResponse<Void>> deleteUserFavouriteByProductId(@PathVariable Long productId) {
        userFavouriteService.deleteUserFavouriteByProductId(productId);
        return ResponseEntity.ok().body(CustomApiResponse.success("success", null));
    }
}
