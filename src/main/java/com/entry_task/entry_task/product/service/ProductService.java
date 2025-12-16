package com.entry_task.entry_task.product.service;

import com.entry_task.entry_task.auth.service.AuthService;
import com.entry_task.entry_task.category.service.CategoryService;
import com.entry_task.entry_task.common.dto.Metadata;
import com.entry_task.entry_task.enums.ProductStatus;
import com.entry_task.entry_task.enums.Role;
import com.entry_task.entry_task.exceptions.ProductNotActiveException;
import com.entry_task.entry_task.exceptions.ProductNotFoundException;
import com.entry_task.entry_task.product.entity.Product;
import com.entry_task.entry_task.product.dto.*;
import com.entry_task.entry_task.product.repository.ProductRepository;
import com.entry_task.entry_task.user.service.UserService;
import com.entry_task.entry_task.product.specifications.ProductSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.entry_task.entry_task.user.entity.User;
import com.entry_task.entry_task.category.entity.Category;

import java.time.Instant;

@Service
public class ProductService {

    private final AuthService authService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;

    public ProductService(AuthService authService, UserService userService, CategoryService categoryService, ProductRepository productRepository) {
        this.authService = authService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.productRepository = productRepository;
    }

    @Cacheable(value = "product:info", key = "#productId")
    public ProductInfo getProductInfo(long productId) {
        return toProductInfo(getActiveProductById(productId));
    }

    public Product getActiveProductById(long productId) {
        Product product = getProductById(productId);
        if (product.getProductStatus() != ProductStatus.ACTIVE) {
            throw new ProductNotActiveException();
        }
        return product;
    }

    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ProductDetailResponse getSellerProductDetail(long productId) {
        Product product = getProductById(productId);
        validateProductOwnership(product);
        return getProductDetail(productId);
    }

    @Cacheable(
            value = "product:list",
            key = "T(java.util.Objects).hash(#request, #sellerId)"
    )
    public ProductListResponse<ProductListing> getUserProductListingList(ProductListRequest request, Long sellerId) {
        if (sellerId != null && sellerId > 0) {
            userService.validateSellerId(sellerId);
        }
        Page<ProductListing> page = getProductListingList(request, sellerId);
        return new ProductListResponse<>(
                page.toList(),
                new Metadata(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getSize(),
                        page.hasNext()
                )
        );
    }

    public ProductListResponse<ProductInfo> getSellerProductInfoList(ProductListRequest request) {
        User currentUser = authService.getCurrentUser();
        Long sellerId = currentUser.getId();
        Page<ProductInfo> page = getProductInfoList(request, sellerId);
        return new ProductListResponse<>(
                page.toList(),
                new Metadata(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getSize(),
                        page.hasNext()
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductListResponse<ProductInfo> getAdminProductInfoList(ProductListRequest request, Long sellerId) {
        if (sellerId != null && sellerId > 0) {
            userService.validateSellerId(sellerId);
        }
        Page<ProductInfo> page = getProductInfoList(request, sellerId);
        return new ProductListResponse<>(
                page.toList(),
                new Metadata(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getSize(),
                        page.hasNext()
                )
        );
    }

    public ProductListResponse<ProductListing> getUserFavouriteProductListingList(ProductListRequest request) {
        Page<ProductListing> page = getUserFavouriteProductListingList(request, authService.getCurrentUser().getId());
        return new ProductListResponse<>(
                page.toList(),
                new Metadata(
                        page.getTotalElements(),
                        page.getNumber(),
                        page.getSize(),
                        page.hasNext()
                )
        );
    }

    @Transactional
    @CacheEvict(value = "product:list", allEntries = true)
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public Long createProduct(CreateProductRequest request) {
        return createProduct(request, authService.getCurrentUser());
    }

    @Transactional
    @CacheEvict(value = "product:list", allEntries = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Long createProductAdmin(CreateProductRequest request, Long sellerId) {
        User user = userService.findUserBySellerId(sellerId);
        return createProduct(request, user);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product:list", allEntries = true),
            @CacheEvict(value = "product:info", key = "#productId")
    })
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public void deactivateProduct(Long productId) {
        Product product = getProductById(productId);
        validateProductOwnership(product);
        setProductStatus(productId, ProductStatus.INACTIVE);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product:list", allEntries = true),
            @CacheEvict(value = "product:info", key = "#productId")
    })
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public void activateProduct(Long productId) {
        Product product = getProductById(productId);
        validateProductOwnership(product);
        setProductStatus(productId, ProductStatus.ACTIVE);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product:list", allEntries = true),
            @CacheEvict(value = "product:info", key = "#productId")
    })
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public void deleteProductById(Long productId) {
        Product product = getProductById(productId);
        validateProductOwnership(product);
        setProductStatus(productId, ProductStatus.DELETED);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product:list", allEntries = true),
            @CacheEvict(value = "product:info", key = "#productId")
    })
    @PreAuthorize("hasRole('SELLER')")
    public void updateProductById(Long productId, UpdateProductRequest request) {
        Product product = getProductById(productId);
        validateProductOwnership(product);
        updateProduct(product, request);
    }

    // Private Functions
    private Product getProductById(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);
    }

    private ProductDetailResponse getProductDetail(long productId) {
        return toProductDetail(getProductById(productId));
    }

    private Page<ProductInfo> getProductInfoList(ProductListRequest request, Long sellerId) {
        Sort.Direction sortDirection = Sort.Direction.fromString(request.sort().order());
        Pageable pageable = PageRequest.of(
                request.pagination().page(),
                request.pagination().size(),
                Sort.by(sortDirection, request.sort().field())
        );
        Specification<Product> specification = Specification.unrestricted();
        if (sellerId != null && sellerId > 0) {
            specification = specification.and(ProductSpecifications.belongsToSeller(sellerId));
        }
        if (request.keyword() != null && !request.keyword().isBlank()) {
            specification = specification.and(ProductSpecifications.nameContains(request.keyword()));
        }
        if (request.filter() != null &&
                request.filter().statuses() != null &&
                !request.filter().statuses().isEmpty()) {
            specification = specification.and(ProductSpecifications.statusIn(request.filter().statuses()));
        }
        if (request.filter() != null &&
                request.filter().categoryIds() != null &&
                !request.filter().categoryIds().isEmpty()) {
            specification = specification.and(ProductSpecifications.categoryIn(request.filter().categoryIds()));
        }
        return productRepository.findAll(specification, pageable).map(this::toProductInfo);
    }

    private Page<ProductListing> getProductListingList(ProductListRequest request, Long sellerId) {
        Sort.Direction sortDirection = Sort.Direction.fromString(request.sort().order());
        Pageable pageable = PageRequest.of(
                request.pagination().page(),
                request.pagination().size(),
                Sort.by(sortDirection, request.sort().field())
        );
        Specification<Product> specification = ProductSpecifications.statusIsActive();
        if (sellerId != null && sellerId > 0) {
            specification = specification.and(ProductSpecifications.belongsToSeller(sellerId));
        }
        if (request.keyword() != null && !request.keyword().isBlank()) {
            specification = specification.and(ProductSpecifications.nameContains(request.keyword()));
        }
        if (request.filter() != null &&
                request.filter().categoryIds() != null &&
                !request.filter().categoryIds().isEmpty()) {
            specification = specification.and(ProductSpecifications.categoryIn(request.filter().categoryIds()));
        }
        return productRepository.findAll(specification, pageable).map(this::toProductListing);
    }

    private Page<ProductListing> getUserFavouriteProductListingList(ProductListRequest request, Long userId) {
        Sort.Direction sortDirection = Sort.Direction.fromString(request.sort().order());
        Pageable pageable = PageRequest.of(
                request.pagination().page(),
                request.pagination().size(),
                Sort.by(sortDirection, request.sort().field())
        );
        Specification<Product> specification = ProductSpecifications.statusIsActive().and(
                ProductSpecifications.isFavouritedBy(userId)
        );
        if (request.keyword() != null && !request.keyword().isBlank()) {
            specification = specification.and(ProductSpecifications.nameContains(request.keyword()));
        }
        if (request.filter() != null &&
                request.filter().categoryIds() != null &&
                !request.filter().categoryIds().isEmpty()) {
            specification = specification.and(ProductSpecifications.categoryIn(request.filter().categoryIds()));
        }
        return productRepository.findAll(specification, pageable).map(this::toProductListing);
    }

    private Long createProduct(CreateProductRequest request, User seller) {
        long now = Instant.now().getEpochSecond();
        Product newProduct = new Product();
        newProduct.setName(request.name());
        newProduct.setSeller(seller);
        newProduct.setPrice(request.price());
        newProduct.setStock(request.stock());
        newProduct.setDescription(request.description());
        newProduct.setProductStatus(ProductStatus.ACTIVE);
        newProduct.setcTime(now);
        newProduct.setmTime(now);
        newProduct.setCategories(categoryService.loadCategories(request.categoryIds()));
        return productRepository.save(newProduct).getId();
    }

    private void updateProduct(Product product, UpdateProductRequest request) {
        long now = Instant.now().getEpochSecond();
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setDescription(request.description());
        product.setmTime(now);
        product.setCategories(categoryService.loadCategories(request.categoryIds()));
        productRepository.save(product);
    }

    private void setProductStatus(Long productId, ProductStatus status) {
        Product product = getProductById(productId);
        validateStatusUpdateOperation(product.getProductStatus(), status);
        product.setProductStatus(status);
        productRepository.save(product);
    }

    private void validateProductOwnership(Product product) {
        User currentUser = authService.getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not own this product");
        }
    }

    private void validateStatusUpdateOperation(ProductStatus before, ProductStatus after) {
        switch (before) {
            case ACTIVE, INACTIVE -> {
                if (before.equals(after)) {
                    throw new IllegalStateException("Product Status is already " + before.name());
                }
            }
            default -> throw new IllegalStateException("Invalid Product Status Change");
        }
    }

    private ProductListing toProductListing(Product product) {
        return new ProductListing(
                product.getId(),
                product.getName(),
                product.getSeller().getId(),
                product.getStock(),
                product.getPrice()
        );
    }

    private ProductInfo toProductInfo(Product product) {
        return new ProductInfo(
                product.getId(),
                product.getName(),
                product.getSeller().getId(),
                product.getStock(),
                product.getPrice(),
                product.getDescription(),
                product.getProductStatus()
        );
    }

    private ProductDetailResponse toProductDetail(Product product) {
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getSeller().getId(),
                product.getStock(),
                product.getPrice(),
                product.getCategories()
                        .stream()
                        .map(Category::getId)
                        .toList(),
                product.getDescription(),
                product.getProductStatus(),
                product.getcTime(),
                product.getmTime()
        );
    }
}
